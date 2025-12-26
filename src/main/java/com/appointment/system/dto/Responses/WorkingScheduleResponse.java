package com.appointment.system.dto.Responses;

import com.appointment.system.model.WorkingSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingScheduleResponse {
    private Long id;
    private Long staffId;
    private String staffName;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isHoliday;
    private java.time.LocalDateTime createdAt;
    
    public static WorkingScheduleResponse from(WorkingSchedule schedule) {
        WorkingScheduleResponse response = new WorkingScheduleResponse();
        response.setId(schedule.getId());
        response.setStaffId(schedule.getStaff().getId());
        response.setStaffName(schedule.getStaff().getName());
        response.setDayOfWeek(schedule.getDayOfWeek());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setHoliday(schedule.isHoliday());
        response.setCreatedAt(schedule.getCreatedAt());
        return response;
    }
}