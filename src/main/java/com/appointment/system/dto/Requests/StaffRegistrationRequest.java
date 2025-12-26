package com.appointment.system.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StaffRegistrationRequest {
    @NotBlank(message = "name must not be blank")
    private String name;
    
    @NotBlank(message = "email must not be blank")
    private String email;
    
    @NotBlank(message = "password must not be blank")
    private String password;
    
    @NotBlank(message = "specialty must not be blank")
    private String specialty;
    
    @NotBlank(message = "license number must not be blank")
    private String licenseNumber;
}