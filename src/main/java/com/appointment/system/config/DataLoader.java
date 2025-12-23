package com.appointment.system.config;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword("password123"); // You should hash this!
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }
    }
}