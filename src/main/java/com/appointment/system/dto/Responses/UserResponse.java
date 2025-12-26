package com.appointment.system.dto.Responses;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    
    // Role-specific fields
    private String specialty;       // For STAFF
    private String licenseNumber;   // For STAFF
    private String companyName;     // For ADMIN
    private String phoneNumber;     // For CUSTOMER
    private String address;         // For CUSTOMER
    private boolean active;
    private java.time.LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setSpecialty(user.getSpecialty());
        response.setLicenseNumber(user.getLicenseNumber());
        response.setCompanyName(user.getCompanyName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}