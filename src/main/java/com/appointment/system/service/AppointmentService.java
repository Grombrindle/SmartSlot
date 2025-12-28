package com.appointment.system.service;

import com.appointment.system.dto.Requests.AppointmentRequestDTO;
import com.appointment.system.dto.Requests.UpdateAppointmentStatusRequestDTO;
import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.exception.BusinessException;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.Appointment;
import com.appointment.system.model.Service;
import com.appointment.system.model.User;
import com.appointment.system.model.WorkingSchedule;
import com.appointment.system.repository.AppointmentRepository;
import com.appointment.system.repository.ServiceRepository;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.WorkingScheduleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    
    public AppointmentService(AppointmentRepository appointmentRepository,
                             UserRepository userRepository,
                             ServiceRepository serviceRepository,
                             WorkingScheduleRepository workingScheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.workingScheduleRepository = workingScheduleRepository;
    }
    
    public Optional<Appointment> getAppointmentById(Long id) {
    return appointmentRepository.findById(id);
}

public void deleteAppointment(Long id) {
    if (!appointmentRepository.existsById(id)) {
        throw new NotFoundException("Appointment not found");
    }
    appointmentRepository.deleteById(id);
}
    public Appointment bookAppointment(AppointmentRequestDTO requestDTO, Long customerId) {
        // Get customer
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        
        // Get staff
        User staff = userRepository.findById(requestDTO.getStaffId())
                .orElseThrow(() -> new NotFoundException("Staff not found"));
        
        // Get service
        Service service = serviceRepository.findById(requestDTO.getServiceId())
                .orElseThrow(() -> new NotFoundException("Service not found"));
        
        // Validate business rules
        validateBookingRules(staff, service, requestDTO.getAppointmentDateTime(), customer);
        
        // Calculate end time
        LocalDateTime endDateTime = requestDTO.getAppointmentDateTime()
                .plusMinutes(service.getDuration());
        
        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setStaff(staff);
        appointment.setService(service);
        appointment.setAppointmentDateTime(requestDTO.getAppointmentDateTime());
        appointment.setEndDateTime(endDateTime);
        appointment.setNotes(requestDTO.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);
        
        return appointmentRepository.save(appointment);
    }
    
    private void validateBookingRules(User staff, Service service, 
                                     LocalDateTime appointmentDateTime, User customer) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Check minimum notice period (24 hours)
        if (appointmentDateTime.isBefore(now.plusHours(24))) {
            throw new BusinessException("Appointments must be booked at least 24 hours in advance");
        }
        
        // 2. Check maximum booking in advance (30 days)
        if (appointmentDateTime.isAfter(now.plusDays(30))) {
            throw new BusinessException("Appointments cannot be booked more than 30 days in advance");
        }
        
        // 3. Check working hours
        validateWorkingHours(staff, appointmentDateTime, service.getDuration());
        
        // 4. Check for overlapping appointments
        checkForOverlappingAppointments(staff, appointmentDateTime, 
                                       appointmentDateTime.plusMinutes(service.getDuration()));
        
        // 5. Additional business rules can be added here
    }
    
    private void validateWorkingHours(User staff, LocalDateTime appointmentDateTime, int duration) {
        LocalDate appointmentDate = appointmentDateTime.toLocalDate();
        DayOfWeek dayOfWeek = appointmentDateTime.getDayOfWeek();
        LocalTime appointmentTime = appointmentDateTime.toLocalTime();
        LocalTime endTime = appointmentTime.plusMinutes(duration);
        
        // Check if staff has schedule for this day
        List<WorkingSchedule> schedules = workingScheduleRepository
                .findByStaffAndDayOfWeekAndIsHolidayFalse(staff, dayOfWeek);
        
        if (schedules.isEmpty()) {
            throw new BusinessException("Staff is not available on " + dayOfWeek);
        }
        
        WorkingSchedule schedule = schedules.get(0);
        
        // Check if appointment is within working hours
        if (appointmentTime.isBefore(schedule.getStartTime()) || 
            endTime.isAfter(schedule.getEndTime())) {
            throw new BusinessException("Appointment must be within working hours: " + 
                                       schedule.getStartTime() + " - " + schedule.getEndTime());
        }
    }
    
    private void checkForOverlappingAppointments(User staff, LocalDateTime start, LocalDateTime end) {
        List<AppointmentStatus> excludedStatuses = Arrays.asList(
            AppointmentStatus.CANCELLED
        );
        
        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
            staff, start, end, excludedStatuses
        );
        
        if (!overlappingAppointments.isEmpty()) {
            throw new BusinessException("Time slot is not available. Please choose another time.");
        }
    }
    
    public Appointment updateAppointmentStatus(Long appointmentId, UpdateAppointmentStatusRequestDTO requestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        AppointmentStatus newStatus = requestDTO.getStatus();
        AppointmentStatus currentStatus = appointment.getStatus();
        
        // Validate status transition
        validateStatusTransition(currentStatus, newStatus);
        
        // Update appointment
        appointment.setStatus(newStatus);
        if (requestDTO.getReason() != null) {
            appointment.setNotes(requestDTO.getReason());
        }
        
        // Set timestamps based on status
        switch (newStatus) {
            case APPROVED:
                appointment.setApprovedAt(LocalDateTime.now());
                break;
            case CANCELLED:
                appointment.setCancelledAt(LocalDateTime.now());
                if (requestDTO.getReason() != null) {
                    appointment.setCancellationReason(requestDTO.getReason());
                }
                break;
            case COMPLETED:
                // Add any completion logic here
                break;
            default:
                break;
        }
        
        return appointmentRepository.save(appointment);
    }
    
    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        // Define valid transitions
        switch (current) {
            case PENDING:
                if (next == AppointmentStatus.CANCELLED || next == AppointmentStatus.APPROVED) {
                    return;
                }
                break;
            case APPROVED:
                if (next == AppointmentStatus.CANCELLED || next == AppointmentStatus.COMPLETED) {
                    return;
                }
                break;
            case CANCELLED:
                throw new BusinessException("Cannot change status of a cancelled appointment");
            case COMPLETED:
                throw new BusinessException("Cannot change status of a completed appointment");
            default:
                throw new BusinessException("Invalid current status: " + current);
        }
        
        throw new BusinessException("Invalid status transition from " + current + " to " + next);
    }
    
    public List<Appointment> getAppointmentsByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        return appointmentRepository.findByCustomer(customer);
    }
    
    public List<Appointment> getAppointmentsByStaff(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new NotFoundException("Staff not found"));
        return appointmentRepository.findByStaff(staff);
    }
    
    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING);
    }
    
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        // Check if appointment can be cancelled (at least 2 hours before)
        if (LocalDateTime.now().isAfter(appointment.getAppointmentDateTime().minusHours(2))) {
            throw new BusinessException("Appointments can only be cancelled at least 2 hours in advance");
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());
        
        appointmentRepository.save(appointment);
    }
    
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end);
    }
}