package com.appointment.system.controller;

import com.appointment.system.dto.Requests.WorkingScheduleRequestDTO;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.AvailableSlotResponse;
import com.appointment.system.dto.Responses.WorkingScheduleResponse;
import com.appointment.system.model.WorkingSchedule;
import com.appointment.system.service.WorkingScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController extends BaseController {
    
    private final WorkingScheduleService workingScheduleService;
    
    public ScheduleController(WorkingScheduleService workingScheduleService) {
        this.workingScheduleService = workingScheduleService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<WorkingScheduleResponse>> createSchedule(
            @Valid @RequestBody WorkingScheduleRequestDTO requestDTO) {
        WorkingSchedule schedule = workingScheduleService.createSchedule(requestDTO);
        return created(WorkingScheduleResponse.from(schedule), "Working schedule created");
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<WorkingScheduleResponse>> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody WorkingScheduleRequestDTO requestDTO) {
        WorkingSchedule schedule = workingScheduleService.updateSchedule(id, requestDTO);
        return ok(WorkingScheduleResponse.from(schedule), "Working schedule updated");
    }
    
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<WorkingScheduleResponse>>> getStaffSchedules(
            @PathVariable Long staffId) {
        List<WorkingSchedule> schedules = workingScheduleService.getSchedulesByStaff(staffId);
        List<WorkingScheduleResponse> response = schedules.stream()
                .map(WorkingScheduleResponse::from).toList();
        return ok(response, "Staff schedules retrieved");
    }
    
    @GetMapping("/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @RequestParam Long staffId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableSlotResponse> slots = workingScheduleService
                .getAvailableSlots(staffId, serviceId, date);
        return ok(slots, "Available time slots retrieved");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long id) {
        workingScheduleService.deleteSchedule(id);
        return ok(null, "Working schedule deleted");
    }
}