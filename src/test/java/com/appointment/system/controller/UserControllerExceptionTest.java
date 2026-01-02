// package com.appointment.system.controller;

// import com.appointment.system.exception.NotFoundException;
// import com.appointment.system.model.User;
// import com.appointment.system.service.UserServiceImpl;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(UserController.class)
// public class UserControllerExceptionTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Mock
//     private UserServiceImpl UserServiceImpl;

//     @Test
//     void getUser_whenNotFound_thenReturns404_withApiResponse() throws Exception {
//         when(UserServiceImpl.getUserById(99L)).thenThrow(new NotFoundException("User not found"));

//         mockMvc.perform(get("/api/users/99").contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.statusCode").value(404))
//                 .andExpect(jsonPath("$.message").value("User not found"))
//                 .andExpect(jsonPath("$.error").value("NOT_FOUND"));
//     }
// }