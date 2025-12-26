package com.appointment.system.config;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create ADMIN user
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            admin.setCompanyName("Appointment System Inc.");
            admin.setActive(true);
            userRepository.save(admin);
        }
        
        // Create STAFF users
        if (userRepository.findByEmail("dr.smith@clinic.com").isEmpty()) {
            User staff1 = new User();
            staff1.setName("Dr. John Smith");
            staff1.setEmail("dr.smith@clinic.com");
            staff1.setPassword(passwordEncoder.encode("staff123"));
            staff1.setRole(UserRole.STAFF);
            staff1.setSpecialty("Dentist");
            staff1.setLicenseNumber("DENT-12345");
            staff1.setActive(true);
            userRepository.save(staff1);
        }
        
        if (userRepository.findByEmail("nurse.jones@clinic.com").isEmpty()) {
            User staff2 = new User();
            staff2.setName("Nurse Sarah Jones");
            staff2.setEmail("nurse.jones@clinic.com");
            staff2.setPassword(passwordEncoder.encode("staff123"));
            staff2.setRole(UserRole.STAFF);
            staff2.setSpecialty("Registered Nurse");
            staff2.setLicenseNumber("RN-67890");
            staff2.setActive(true);
            userRepository.save(staff2);
        }
        
        // Create CUSTOMER users
        if (userRepository.findByEmail("customer1@example.com").isEmpty()) {
            User customer1 = new User();
            customer1.setName("Alice Johnson");
            customer1.setEmail("customer1@example.com");
            customer1.setPassword(passwordEncoder.encode("customer123"));
            customer1.setRole(UserRole.CUSTOMER);
            customer1.setPhoneNumber("+1-555-0101");
            customer1.setAddress("123 Main St, New York, NY");
            customer1.setActive(true);
            userRepository.save(customer1);
        }
        
        if (userRepository.findByEmail("customer2@example.com").isEmpty()) {
            User customer2 = new User();
            customer2.setName("Bob Williams");
            customer2.setEmail("customer2@example.com");
            customer2.setPassword(passwordEncoder.encode("customer123"));
            customer2.setRole(UserRole.CUSTOMER);
            customer2.setPhoneNumber("+1-555-0102");
            customer2.setAddress("456 Oak Ave, Los Angeles, CA");
            customer2.setActive(true);
            userRepository.save(customer2);
        }
    }
}