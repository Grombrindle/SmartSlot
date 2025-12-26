package com.appointment.system.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long staffId;
    private String staffName;
    private Long serviceId;
    private String serviceName;
}