package com.appointment.system.config;

import com.appointment.system.model.Service;
import com.appointment.system.model.User;
import com.appointment.system.repository.AppointmentRepository;
import com.appointment.system.repository.ServiceRepository;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.WorkingScheduleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    @Test
    void run_createsAdminWithHashedPassword_whenNotPresent() throws Exception {
        UserRepository userRepo = mock(UserRepository.class);
        ServiceRepository serviceRepo = mock(ServiceRepository.class);
        WorkingScheduleRepository wsRepo = mock(WorkingScheduleRepository.class);
        AppointmentRepository apptRepo = mock(AppointmentRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        when(userRepo.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(serviceRepo.count()).thenReturn(0L);
        when(wsRepo.count()).thenReturn(0L);
        when(apptRepo.count()).thenReturn(0L);
        // Provide at least 3 services so DataLoader can access indices 0 and 2
        Service s1 = new Service(); s1.setDuration(60);
        Service s2 = new Service(); s2.setDuration(90);
        Service s3 = new Service(); s3.setDuration(30);
        when(serviceRepo.findByIsActiveTrue()).thenReturn(java.util.List.of(s1, s2, s3));

        DataLoader loader = new DataLoader(userRepo, serviceRepo, wsRepo, apptRepo, encoder);

        loader.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, atLeastOnce()).save(captor.capture());
        java.util.List<User> savedUsers = captor.getAllValues();
        User saved = savedUsers.stream().filter(u -> "admin@example.com".equals(u.getEmail())).findFirst().orElse(null);

        assertThat(saved).isNotNull();
        assertThat(saved.getPassword()).isNotEqualTo("admin123");
        assertThat(saved.getPassword()).matches("^\\$2[aby]\\$\\d{2}\\$.*");
    }
} 