package com.appointment.system.service.interfaces;

import com.appointment.system.dto.Requests.AppointmentRequestDTO;
import com.appointment.system.dto.Requests.UpdateAppointmentStatusRequestDTO;
import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment bookAppointment(AppointmentRequestDTO requestDTO, Long customerId);
    
    Appointment updateAppointmentStatus(Long appointmentId, UpdateAppointmentStatusRequestDTO requestDTO);
    
    Optional<Appointment> getAppointmentById(Long id);
    
    void deleteAppointment(Long id);
    
    List<Appointment> getAppointmentsByCustomer(Long customerId);
    
    List<Appointment> getAppointmentsByStaff(Long staffId);
    
    List<Appointment> getPendingAppointments();
    
    List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    void cancelAppointment(Long appointmentId, String reason);
}