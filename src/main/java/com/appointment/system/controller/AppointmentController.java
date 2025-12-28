package com.appointment.system.controller;

import com.appointment.system.dto.Requests.AppointmentRequestDTO;
import com.appointment.system.dto.Requests.UpdateAppointmentStatusRequestDTO;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.AppointmentResponse;
import com.appointment.system.dto.Responses.AvailableSlotResponse;
import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.exception.BusinessException;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.Appointment;
import com.appointment.system.service.AppointmentService;
import com.appointment.system.service.UserService;
import com.appointment.system.service.WorkingScheduleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController extends BaseController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final WorkingScheduleService workingScheduleService;

    public AppointmentController(AppointmentService appointmentService, 
                               UserService userService,
                               WorkingScheduleService workingScheduleService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.workingScheduleService = workingScheduleService;

    }

    // ======================
    // ADMIN ENDPOINTS
    // ======================
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> getAllAppointments(
            @PageableDefault(sort = "appointmentDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        

        LocalDateTime startDate = from != null ? from.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = to != null ? to.plusDays(1).atStartOfDay().minusSeconds(1) : LocalDateTime.now().plusMonths(1);
        
        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        
        if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus statusEnum = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointments.stream()
                        .filter(a -> a.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }
        
        // Convert to responses and paginate
        List<AppointmentResponse> responses = appointments.stream()
                .map(AppointmentResponse::from)
                .collect(Collectors.toList());
        
        // Proper pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        Page<AppointmentResponse> page = new PageImpl<>(
                responses.subList(start, end), pageable, responses.size());
        
        return ok(page, "Appointments retrieved successfully");
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        return ok(AppointmentResponse.from(appointment), "Appointment retrieved successfully");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ok(null, "Appointment deleted successfully");
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequestDTO requestDTO) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, requestDTO);
        return ok(AppointmentResponse.from(appointment), "Appointment status updated successfully");
    }

    // ======================
    // CUSTOMER ENDPOINTS
    // ======================
    
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO requestDTO) {
        Long customerId = getCurrentUserId();
        Appointment appointment = appointmentService.bookAppointment(requestDTO, customerId);
        return created(AppointmentResponse.from(appointment), "Appointment booked successfully");
    }
    
   @GetMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getCustomerAppointments() {
    Long customerId = getCurrentUserId();
    List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customerId);
    List<AppointmentResponse> responses = appointments.stream()
            .map(AppointmentResponse::from)
            .collect(Collectors.toList());
    return ok(responses, "Customer appointments retrieved successfully");
}
    
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Long id,
            @RequestParam String reason) {
        // Verify ownership first
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        if (!appointment.getCustomer().getId().equals(getCurrentUserId())) {
            throw new BusinessException("You can only cancel your own appointments");
        }
        
        appointmentService.cancelAppointment(id, reason);
        return ok(AppointmentResponse.from(appointment), "Appointment cancelled successfully");
    }

    // ======================
    // STAFF ENDPOINTS
    // ======================
    
    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getStaffAppointments() {
        Long staffId = getCurrentUserId();
        List<Appointment> appointments = appointmentService.getAppointmentsByStaff(staffId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(AppointmentResponse::from)
                .collect(Collectors.toList());
        return ok(responses, "Staff appointments retrieved successfully");
    }
    
    @GetMapping("/staff/pending")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getPendingAppointments() {
        List<Appointment> appointments = appointmentService.getPendingAppointments();
        List<AppointmentResponse> responses = appointments.stream()
                .filter(a -> a.getStaff() != null && a.getStaff().getId().equals(getCurrentUserId()))
                .map(AppointmentResponse::from)
                .collect(Collectors.toList());
        return ok(responses, "Pending appointments retrieved successfully");
    }

    // ======================
    // COMMON ENDPOINTS
    // ======================
    
    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @RequestParam Long staffId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableSlotResponse> slots = workingScheduleService.getAvailableSlots(staffId, serviceId, date);
        return ok(slots, "Available time slots retrieved");
    }

    // Helper method to get current user ID
   private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new BusinessException("User not authenticated");
    }
    
    // Get the email from authentication (this is likely what's in your JWT token)
    String email = authentication.getName();
    
    
    // Find user by email and get their ID
    return userService.getUserByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found with email: " + email))
            .getId();
}
}