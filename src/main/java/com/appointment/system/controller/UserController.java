package com.appointment.system.controller;

import com.appointment.system.dto.ApiResponse;
import com.appointment.system.dto.UserRequest;
import com.appointment.system.dto.UserResponse;
import com.appointment.system.model.User;
import com.appointment.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends com.appointment.system.controller.BaseController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@jakarta.validation.Valid @RequestBody UserRequest userRequest) {
        // Map request DTO -> entity (keep mapping simple for now)
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setRole(userRequest.getRole());

        User createdUser = userService.createUser(user);
        return ok(UserResponse.from(createdUser), "User created");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ok(UserResponse.from(user), "User retrieved");
    }
}