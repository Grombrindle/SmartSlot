package com.appointment.system.dto.Requests;

import com.appointment.system.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusRequestDTO {
    @NotNull(message = "Status is required")
    private AppointmentStatus status;
    
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason; // For cancellation or notes
}