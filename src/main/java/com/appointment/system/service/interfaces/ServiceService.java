package com.appointment.system.service.interfaces;

import com.appointment.system.dto.Requests.ServiceRequestDTO;
import com.appointment.system.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceService {
    Service createService(ServiceRequestDTO requestDTO);
    
    Service updateService(Long id, ServiceRequestDTO requestDTO);
    
    Service getServiceById(Long id);
    
    Page<Service> getAllActiveServices(Pageable pageable);
    
    List<Service> getServicesByProvider(Long providerId);
    
    void deactivateService(Long id);
    
    boolean isServiceAvailable(Long serviceId);
}