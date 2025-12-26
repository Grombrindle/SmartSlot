package com.appointment.system.model;

import com.appointment.system.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // ADMIN, STAFF, CUSTOMER
    
    // Role-specific fields (nullable - only used when relevant)
    private String specialty;       // For STAFF only (e.g., "Dentist", "Cardiologist")
    private String licenseNumber;   // For STAFF only
    private String companyName;     // For ADMIN only (if needed)
    private String phoneNumber;     // For CUSTOMER only
    private String address;         // For CUSTOMER only
    
    // Additional common fields
    private boolean isActive = true;
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}