package com.appointment.system.dto.Responses;

import com.appointment.system.enums.UserRole;
import lombok.Data;

@Data
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private String specialty;
    private String licenseNumber;
    private boolean isActive;
    
    public static StaffResponse fromUser(com.appointment.system.model.User user) {
        StaffResponse response = new StaffResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setSpecialty(user.getSpecialty());
        response.setLicenseNumber(user.getLicenseNumber());
        response.setActive(user.isActive());
        return response;
    }
}