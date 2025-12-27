package com.appointment.system.controller;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import com.appointment.system.model.WorkingSchedule;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.WorkingScheduleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScheduleController {

    private final WorkingScheduleRepository workingScheduleRepository;
    private final UserRepository userRepository;

    public AdminScheduleController(WorkingScheduleRepository workingScheduleRepository, 
                                  UserRepository userRepository) {
        this.workingScheduleRepository = workingScheduleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/schedules")
    public String adminSchedules(Model model) {
        // Get all staff members (STAFF role only)
        List<User> staffMembers = userRepository.findAll().stream()
                .filter(user -> UserRole.STAFF.equals(user.getRole()))
                .collect(Collectors.toList());
        
        // Get all schedules
        List<WorkingSchedule> schedules = workingScheduleRepository.findAll();
        
        // Pre-calculate staff statistics
        Map<Long, Long> staffWorkingDays = schedules.stream()
                .filter(schedule -> !schedule.isHoliday())
                .collect(Collectors.groupingBy(
                    schedule -> schedule.getStaff().getId(), 
                    Collectors.counting()
                ));
        
        // Pre-calculate day coverage
        Map<String, Long> dayCoverage = schedules.stream()
                .filter(schedule -> !schedule.isHoliday())
                .collect(Collectors.groupingBy(
                    schedule -> schedule.getDayOfWeek().name(),
                    Collectors.counting()
                ));
        
        model.addAttribute("staffMembers", staffMembers);
        model.addAttribute("schedules", schedules);
        model.addAttribute("staffWorkingDays", staffWorkingDays);
        model.addAttribute("dayCoverage", dayCoverage);
        
        return "admin-schedules";
    }
}