package com.appointment.system.controller;

import com.appointment.system.dto.Requests.AuthRequest;
import com.appointment.system.dto.Requests.CustomerRegistrationRequest;
import com.appointment.system.dto.Requests.StaffRegistrationRequest;
import com.appointment.system.dto.Responses.ApiResponse;
import com.appointment.system.dto.Responses.AuthResponse;
import com.appointment.system.dto.Responses.CustomerResponse;
import com.appointment.system.dto.Responses.StaffResponse;
import com.appointment.system.model.User;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.security.JwtUtil;
import com.appointment.system.service.impl.CustomerServiceImpl;
import com.appointment.system.service.impl.StaffServiceImpl;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final StaffServiceImpl StaffServiceImpl;
    private final CustomerServiceImpl CustomerServiceImpl;
    
    public AuthController(AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil,
                         UserRepository userRepository,
                         StaffServiceImpl StaffServiceImpl,
                         CustomerServiceImpl CustomerServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.StaffServiceImpl = StaffServiceImpl;
        this.CustomerServiceImpl = CustomerServiceImpl;
    }
    
    // @PostMapping("/login")
    // public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
    //     try {
    //         // Authenticate user
    //         Authentication authentication = authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(
    //                 authRequest.getEmail(),
    //                 authRequest.getPassword()
    //             )
    //         );
            
    //         // Get user details
    //         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
    //         // Generate JWT token
    //         String token = jwtUtil.generateToken(userDetails.getUsername());
            
    //         // Get user from database
    //         User user = userRepository.findByEmail(userDetails.getUsername())
    //                 .orElseThrow(() -> new RuntimeException("User not found"));
            
    //         // Create response
    //         AuthResponse authResponse = new AuthResponse(
    //             token,
    //             user.getEmail(),
    //             user.getRole().name(),
    //             user.getId()
    //         );
            
    //         return ok(authResponse, "Login successful");
            
    //     } catch (BadCredentialsException e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body(new ApiResponse<>(false, null, 401, "Invalid email or password", "UNAUTHORIZED"));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body(new ApiResponse<>(false, null, 500, "Login failed: " + e.getMessage(), "INTERNAL_ERROR"));
    //     }
    // }
    
    @PostMapping("/login")
public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            )
        );
        
        // Create session for web users
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token for API requests
        String token = jwtUtil.generateToken(authentication.getName());
        
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        AuthResponse authResponse = new AuthResponse(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getId()
        );
        
        return ok(authResponse, "Login successful");
        
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, null, 401, "Invalid email or password", "UNAUTHORIZED"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, 500, "Login failed: " + e.getMessage(), "INTERNAL_ERROR"));
    }
}
    @PostMapping("/register/staff")
    public ResponseEntity<ApiResponse<StaffResponse>> registerStaff(@Valid @RequestBody StaffRegistrationRequest request) {
        User staffUser = StaffServiceImpl.registerStaff(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getSpecialty(),
            request.getLicenseNumber()
        );
        return created(StaffResponse.fromUser(staffUser), "Staff member registered successfully");
    }
    
    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        User customerUser = CustomerServiceImpl.registerCustomer(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            request.getPhoneNumber(),
            request.getAddress()
        );
        return created(CustomerResponse.fromUser(customerUser), "Customer registered successfully");
    }
}