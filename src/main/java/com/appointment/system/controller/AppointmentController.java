package com.appointment.system.controller;

import com.appointment.system.dto.Requests.AppointmentRequestDTO;
import com.appointment.system.dto.Requests.UpdateAppointmentStatusRequestDTO;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.AppointmentResponse;
import com.appointment.system.model.Appointment;
import com.appointment.system.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController extends BaseController {
    
    private final AppointmentService appointmentService;
    
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    @PostMapping("/book")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO requestDTO) {
        // Get current user ID from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long customerId = getCurrentUserId(authentication);
        
        Appointment appointment = appointmentService.bookAppointment(requestDTO, customerId);
        return created(AppointmentResponse.from(appointment), "Appointment booked successfully");
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(
            LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusMonths(1));
        List<AppointmentResponse> response = appointments.stream()
            .map(AppointmentResponse::from).toList();
        return ok(response, "Appointments retrieved");
    }
    
    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long customerId = getCurrentUserId(authentication);
        
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customerId);
        List<AppointmentResponse> response = appointments.stream()
            .map(AppointmentResponse::from).toList();
        return ok(response, "Your appointments retrieved");
    }
    
    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByStaff(
            @PathVariable Long staffId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStaff(staffId);
        List<AppointmentResponse> response = appointments.stream()
            .map(AppointmentResponse::from).toList();
        return ok(response, "Staff appointments retrieved");
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getPendingAppointments() {
        List<Appointment> appointments = appointmentService.getPendingAppointments();
        List<AppointmentResponse> response = appointments.stream()
            .map(AppointmentResponse::from).toList();
        return ok(response, "Pending appointments retrieved");
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequestDTO requestDTO) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, requestDTO);
        return ok(AppointmentResponse.from(appointment), "Appointment status updated");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        appointmentService.cancelAppointment(id, reason != null ? reason : "Cancelled by user");
        return ok(null, "Appointment cancelled successfully");
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointment(@PathVariable Long id) {
        // Note: You should add authorization to check if user has access to this appointment
        // This is simplified for the example
        return ResponseEntity.notFound().build();
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        // Extract user ID from authentication
        // This is a simplified version - you might need to adjust based on your UserDetails implementation
        String email = authentication.getName();
        // In a real implementation, you would fetch the user ID from the database
        // For now, we'll return a placeholder
        return 1L; // Replace with actual user ID retrieval
    }
}