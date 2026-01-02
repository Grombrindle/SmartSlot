package com.appointment.system.service.interfaces;

import com.appointment.system.model.User;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    
    User getUserById(Long id);
    
    Optional<User> getUserByEmail(String email);
}