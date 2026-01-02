package com.appointment.system.service.interfaces;

import com.appointment.system.dto.Requests.WorkingScheduleRequestDTO;
import com.appointment.system.dto.Responses.AvailableSlotResponse;
import com.appointment.system.model.WorkingSchedule;

import java.time.LocalDate;
import java.util.List;

public interface WorkingScheduleService {
    WorkingSchedule createSchedule(WorkingScheduleRequestDTO requestDTO);
    
    WorkingSchedule updateSchedule(Long id, WorkingScheduleRequestDTO requestDTO);
    
    List<WorkingSchedule> getSchedulesByStaff(Long staffId);
    
    void deleteSchedule(Long id);
    
    List<AvailableSlotResponse> getAvailableSlots(Long staffId, Long serviceId, LocalDate date);
}