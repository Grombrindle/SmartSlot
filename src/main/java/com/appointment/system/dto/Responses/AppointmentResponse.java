package com.appointment.system.dto.Responses;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.Appointment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long staffId;
    private String staffName;
    private Long serviceId;
    private String serviceName;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private AppointmentStatus status;
    private String notes;
    private String cancellationReason;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    
    public static AppointmentResponse from(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setCustomerId(appointment.getCustomer().getId());
        response.setCustomerName(appointment.getCustomer().getName());
        if (appointment.getStaff() != null) {
            response.setStaffId(appointment.getStaff().getId());
            response.setStaffName(appointment.getStaff().getName());
        }
        response.setServiceId(appointment.getService().getId());
        response.setServiceName(appointment.getService().getName());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setEndDateTime(appointment.getEndDateTime());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setCancellationReason(appointment.getCancellationReason());
        response.setApprovedAt(appointment.getApprovedAt());
        response.setCancelledAt(appointment.getCancelledAt());
        return response;
    }
}