package com.appointment.system.dto.Requests;

import com.appointment.system.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "email must not be blank")
    @Email(message = "must be a valid email")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    private UserRole role;
    
    // Role-specific fields
    private String specialty;       // For STAFF
    private String licenseNumber;   // For STAFF
    private String companyName;     // For ADMIN
    private String phoneNumber;     // For CUSTOMER
    private String address;         // For CUSTOMER
}