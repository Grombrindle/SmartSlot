package com.appointment.system.controller;

import com.appointment.system.dto.Requests.UserRequest;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.UserResponse;
import com.appointment.system.exception.NotFoundException;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends com.appointment.system.controller.BaseController {
    
    @Autowired
    private UserServiceImpl UserServiceImpl;
    @Autowired
     private UserRepository userRepository;
    
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@jakarta.validation.Valid @RequestBody UserRequest userRequest) {
        // Map request DTO -> entity (keep mapping simple for now)
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());

        User createdUser = UserServiceImpl.createUser(user);
        return ok(UserResponse.from(createdUser), "User created");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        User user = UserServiceImpl.getUserById(id);
        return ok(UserResponse.from(user), "User retrieved");
    }
    @GetMapping("/profile")
public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found"));
    
    return ok(UserResponse.from(user), "User profile retrieved");
}
}