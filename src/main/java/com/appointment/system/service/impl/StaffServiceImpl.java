package com.appointment.system.service.impl;

import com.appointment.system.enums.UserRole;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.service.interfaces.StaffService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerStaff(String name, String email, String password, 
                             String specialty, String licenseNumber) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User staff = new User();
        staff.setName(name);
        staff.setEmail(email);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(UserRole.STAFF);
        staff.setSpecialty(specialty);
        staff.setLicenseNumber(licenseNumber);
        staff.setActive(true);

        return userRepository.save(staff);
    }

    @Override
    public User getStaffById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.STAFF)
                .orElseThrow(() -> new NotFoundException("Staff member not found"));
    }
}