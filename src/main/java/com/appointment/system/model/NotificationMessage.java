package com.appointment.system.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.appointment.system.enums.AppointmentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String type; // APPOINTMENT_UPDATE, APPOINTMENT_CREATED, etc.
    private String message;
    private Long appointmentId;
    private AppointmentStatus appointmentStatus;
    private Long userId;
    private LocalDateTime timestamp;
}