package com.appointment.system.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRegistrationRequest {
    @NotBlank(message = "name must not be blank")
    private String name;
    
    @NotBlank(message = "email must not be blank")
    private String email;
    
    @NotBlank(message = "password must not be blank")
    private String password;
    
    private String phoneNumber;
    private String address;
}