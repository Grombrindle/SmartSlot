package com.appointment.system.service;

import com.appointment.system.dto.Requests.ServiceRequestDTO;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.exception.ValidationException;
import com.appointment.system.model.Service;
import com.appointment.system.model.User;
import com.appointment.system.enums.UserRole;
import com.appointment.system.repository.ServiceRepository;
import com.appointment.system.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {
    
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    
    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }
    
    public Service createService(ServiceRequestDTO requestDTO) {
        User provider = userRepository.findById(requestDTO.getProviderId())
                .orElseThrow(() -> new NotFoundException("Staff provider not found"));
        
        // Validate that provider is STAFF
        if (provider.getRole() != UserRole.STAFF) {
            throw new ValidationException("Provider must have STAFF role. User has role: " + provider.getRole());
        }
        
        Service service = new Service();
        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setDuration(requestDTO.getDuration());
        service.setPrice(requestDTO.getPrice());
        service.setProvider(provider);
        
        return serviceRepository.save(service);
    }
    
    public Service updateService(Long id, ServiceRequestDTO requestDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        
        if (requestDTO.getProviderId() != null && 
            !requestDTO.getProviderId().equals(service.getProvider().getId())) {
            User newProvider = userRepository.findById(requestDTO.getProviderId())
                    .orElseThrow(() -> new NotFoundException("New provider not found"));
            
            // Validate that new provider is STAFF
            if (newProvider.getRole() != UserRole.STAFF) {
                throw new ValidationException("Provider must have STAFF role. User has role: " + newProvider.getRole());
            }
            service.setProvider(newProvider);
        }
        
        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setDuration(requestDTO.getDuration());
        service.setPrice(requestDTO.getPrice());
        
        return serviceRepository.save(service);
    }
    
    public Service getServiceById(Long id) {
        return serviceRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
    }
    
    public Page<Service> getAllActiveServices(Pageable pageable) {
        return serviceRepository.findByIsActiveTrue(pageable);
    }
    
    public List<Service> getServicesByProvider(Long providerId) {
        // Optional: Validate provider is STAFF when fetching by provider
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provider not found"));
        
        if (provider.getRole() != UserRole.STAFF) {
            throw new ValidationException("User is not a STAFF member");
        }
        
        return serviceRepository.findByProviderIdAndIsActiveTrue(providerId);
    }
    
    public void deactivateService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
        service.setActive(false);
        serviceRepository.save(service);
    }
    
    public boolean isServiceAvailable(Long serviceId) {
        return serviceRepository.findByIdAndIsActiveTrue(serviceId).isPresent();
    }
    
    // Optional: Helper method to validate provider
    private void validateProvider(User provider) {
        if (provider.getRole() != UserRole.STAFF) {
            throw new ValidationException("Provider must have STAFF role. User has role: " + provider.getRole());
        }
    }
}