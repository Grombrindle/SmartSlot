package com.appointment.system.dto.Responses;

import com.appointment.system.model.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private Double price;
    private Long providerId;
    private String providerName;
    private boolean active;
    private java.time.LocalDateTime createdAt;
    
    public static ServiceResponse from(Service service) {
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setDescription(service.getDescription());
        response.setDuration(service.getDuration());
        response.setPrice(service.getPrice());
        response.setProviderId(service.getProvider().getId());
        response.setProviderName(service.getProvider().getName());
        response.setActive(service.isActive());
        response.setCreatedAt(service.getCreatedAt());
        return response;
    }
}