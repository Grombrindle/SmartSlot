package com.appointment.system.service;

import com.appointment.system.dto.Requests.ServiceRequestDTO;
import com.appointment.system.enums.UserRole;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.exception.ValidationException;
import com.appointment.system.model.Service;
import com.appointment.system.model.User;
import com.appointment.system.repository.ServiceRepository;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.service.impl.ServiceServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceServiceImplTest {

    @Test
    void createService_success() {
        ServiceRepository serviceRepo = mock(ServiceRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        ServiceServiceImpl svc = new ServiceServiceImpl(serviceRepo, userRepo);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setName("Haircut");
        dto.setDescription("desc");
        dto.setDuration(30);
        dto.setPrice(20.0);
        dto.setProviderId(11L);

        User provider = new User();
        provider.setId(11L);
        provider.setRole(UserRole.STAFF);

        when(userRepo.findById(11L)).thenReturn(Optional.of(provider));

        Service saved = new Service();
        saved.setId(2L);
        when(serviceRepo.save(any(Service.class))).thenReturn(saved);

        Service result = svc.createService(dto);
        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void createService_providerNotFound_throwsNotFound() {
        ServiceRepository serviceRepo = mock(ServiceRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        ServiceServiceImpl svc = new ServiceServiceImpl(serviceRepo, userRepo);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setProviderId(99L);

        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> svc.createService(dto));
    }

    @Test
    void createService_providerNotStaff_throwsValidation() {
        ServiceRepository serviceRepo = mock(ServiceRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        ServiceServiceImpl svc = new ServiceServiceImpl(serviceRepo, userRepo);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setProviderId(12L);

        User provider = new User();
        provider.setId(12L);
        provider.setRole(UserRole.CUSTOMER);

        when(userRepo.findById(12L)).thenReturn(Optional.of(provider));

        assertThrows(ValidationException.class, () -> svc.createService(dto));
    }

    @Test
    void getServiceById_notFound_throwsNotFound() {
        ServiceRepository serviceRepo = mock(ServiceRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        ServiceServiceImpl svc = new ServiceServiceImpl(serviceRepo, userRepo);

        when(serviceRepo.findByIdAndIsActiveTrue(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> svc.getServiceById(5L));
    }
}
