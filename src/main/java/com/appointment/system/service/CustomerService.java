package com.appointment.system.service;

import com.appointment.system.enums.UserRole;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerCustomer(String name, String email, String password, String phoneNumber, String address) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User customer = new User();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setRole(UserRole.CUSTOMER);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        customer.setActive(true);

        return userRepository.save(customer);
    }

    public User getCustomerById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }
}