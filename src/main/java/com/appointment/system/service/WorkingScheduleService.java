package com.appointment.system.service;

import com.appointment.system.dto.Requests.WorkingScheduleRequestDTO;
import com.appointment.system.dto.Responses.AvailableSlotResponse;
import com.appointment.system.exception.BusinessException;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.User;
import com.appointment.system.model.WorkingSchedule;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.WorkingScheduleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
public class WorkingScheduleService {
    
    private final WorkingScheduleRepository workingScheduleRepository;
    private final UserRepository userRepository;
    
    public WorkingScheduleService(WorkingScheduleRepository workingScheduleRepository, 
                                  UserRepository userRepository) {
        this.workingScheduleRepository = workingScheduleRepository;
        this.userRepository = userRepository;
    }
    
    public WorkingSchedule createSchedule(WorkingScheduleRequestDTO requestDTO) {
        User staff = userRepository.findById(requestDTO.getStaffId())
                .orElseThrow(() -> new NotFoundException("Staff not found"));
        
        // Check if schedule already exists for this day
        Optional<WorkingSchedule> existingSchedule = workingScheduleRepository
                .findByStaffAndDayOfWeek(staff, requestDTO.getDayOfWeek());
        
        if (existingSchedule.isPresent()) {
            throw new BusinessException("Schedule already exists for this day");
        }
        
        // Validate time range
        if (!requestDTO.getStartTime().isBefore(requestDTO.getEndTime())) {
            throw new BusinessException("Start time must be before end time");
        }
        
        WorkingSchedule schedule = new WorkingSchedule();
        schedule.setStaff(staff);
        schedule.setDayOfWeek(requestDTO.getDayOfWeek());
        schedule.setStartTime(requestDTO.getStartTime());
        schedule.setEndTime(requestDTO.getEndTime());
        schedule.setHoliday(requestDTO.isHoliday());
        
        return workingScheduleRepository.save(schedule);
    }
    
    public WorkingSchedule updateSchedule(Long id, WorkingScheduleRequestDTO requestDTO) {
        WorkingSchedule schedule = workingScheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Working schedule not found"));
        
        // Validate time range
        if (!requestDTO.getStartTime().isBefore(requestDTO.getEndTime())) {
            throw new BusinessException("Start time must be before end time");
        }
        
        schedule.setStartTime(requestDTO.getStartTime());
        schedule.setEndTime(requestDTO.getEndTime());
        schedule.setHoliday(requestDTO.isHoliday());
        
        return workingScheduleRepository.save(schedule);
    }
    
    public List<WorkingSchedule> getSchedulesByStaff(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Staff not found"));
        return workingScheduleRepository.findByStaffAndIsHolidayFalse(staff);
    }
    
    public void deleteSchedule(Long id) {
        if (!workingScheduleRepository.existsById(id)) {
            throw new NotFoundException("Working schedule not found");
        }
        workingScheduleRepository.deleteById(id);
    }
    
    public List<AvailableSlotResponse> getAvailableSlots(Long staffId, Long serviceId, LocalDate date) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Staff not found"));
        
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<WorkingSchedule> schedules = workingScheduleRepository
                .findByStaffAndDayOfWeekAndIsHolidayFalse(staff, dayOfWeek);
        
        if (schedules.isEmpty()) {
            return new ArrayList<>();
        }
        
        WorkingSchedule schedule = schedules.get(0);
        List<AvailableSlotResponse> availableSlots = new ArrayList<>();
        
        // For simplicity, we'll generate 30-minute slots
        LocalTime slotDuration = LocalTime.of(0, 30);
        LocalTime currentTime = schedule.getStartTime();
        
        while (currentTime.plusMinutes(30).isBefore(schedule.getEndTime()) || 
               currentTime.plusMinutes(30).equals(schedule.getEndTime())) {
            LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
            LocalDateTime slotEnd = slotStart.plusMinutes(30);
            
            AvailableSlotResponse slot = new AvailableSlotResponse();
            slot.setStartTime(slotStart);
            slot.setEndTime(slotEnd);
            slot.setStaffId(staffId);
            slot.setStaffName(staff.getName());
            slot.setServiceId(serviceId);
            
            availableSlots.add(slot);
            
            currentTime = currentTime.plusMinutes(30);
        }
        
        return availableSlots;
    }
}