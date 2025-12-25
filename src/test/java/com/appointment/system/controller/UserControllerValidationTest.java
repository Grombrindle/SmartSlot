package com.appointment.system.controller;

import com.appointment.system.enums.UserRole;
import com.appointment.system.model.User;
import com.appointment.system.service.UserService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Test
    void createUser_whenInvalidInput_thenReturns400_andApiResponseStructure() throws Exception {
        String payload = "{\"name\" : \"\", \"email\" : \"not-an-email\", \"password\" : \"123\", \"role\" : \"CUSTOMER\"}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.result.email").value("john@example.com"))
                .andExpect(jsonPath("$.result.password").doesNotExist());
    }
}