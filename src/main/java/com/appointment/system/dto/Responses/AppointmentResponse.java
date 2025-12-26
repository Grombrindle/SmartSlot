package com.appointment.system.dto.Responses;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long staffId;
    private String staffName;
    private Long serviceId;
    private String serviceName;
    private Integer serviceDuration;
    private Double servicePrice;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private AppointmentStatus status;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    
    public static AppointmentResponse from(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setCustomerId(appointment.getCustomer().getId());
        response.setCustomerName(appointment.getCustomer().getName());
        response.setStaffId(appointment.getStaff().getId());
        response.setStaffName(appointment.getStaff().getName());
        response.setServiceId(appointment.getService().getId());
        response.setServiceName(appointment.getService().getName());
        response.setServiceDuration(appointment.getService().getDuration());
        response.setServicePrice(appointment.getService().getPrice());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setEndDateTime(appointment.getEndDateTime());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setCancellationReason(appointment.getCancellationReason());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        response.setApprovedAt(appointment.getApprovedAt());
        response.setCancelledAt(appointment.getCancelledAt());
        return response;
    }
}