package com.appointment.system.dto.Responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailableSlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long staffId;
    private String staffName;
    private Long serviceId;
}