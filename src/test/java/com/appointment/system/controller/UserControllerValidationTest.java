package com.appointment.system.controller;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import com.appointment.system.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.appointment.system.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
public class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @MockitoBean
    private UserServiceImpl userService; 

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private com.appointment.system.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.appointment.system.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void createUser_whenInvalidInput_thenReturns400_andApiResponseStructure() throws Exception {
        String payload = "{\"name\" : \"\", \"email\" : \"not-an-email\", \"password\" : \"123\", \"role\" : \"CUSTOMER\"}";

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void createUser_whenValid_thenReturns200_andApiResponseHasNoPassword() throws Exception {
        String payload = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
            put("name", "John");
            put("email", "john@example.com");
            put("password", "secret123");
            put("role", "CUSTOMER");
        }});

        User saved = new User();
        saved.setId(1L);
        saved.setName("John");
        saved.setEmail("john@example.com");
        saved.setPassword("secret123");
        saved.setRole(UserRole.CUSTOMER);

        when(userService.createUser(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.result.email").value("john@example.com"))
                .andExpect(jsonPath("$.result.password").doesNotExist());
    }
} 