package com.appointment.system.controller;

import com.appointment.system.dto.Requests.AuthRequest;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.AuthResponse;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    public AuthController(AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil,
                         UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(),
                    authRequest.getPassword()
                )
            );
            
            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername());
            
            // Get user from database
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Create response
            AuthResponse authResponse = new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId()
            );
            
            return ok(authResponse, "Login successful");
            
        } catch (BadCredentialsException e) {
            return unauthorized(null, "Invalid email or password");
        } catch (Exception e) {
            return error(500, null, "Login failed: " + e.getMessage());
        }
    }

    private ResponseEntity<ApiResponse<AuthResponse>> unauthorized(Object object, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unauthorized'");
    }
}