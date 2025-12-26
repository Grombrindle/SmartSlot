// package com.appointment.system.config;

// import com.appointment.system.model.User;
// import com.appointment.system.repository.UserRepository;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.Mockito.*;

// class DataLoaderTest {

//     @Test
//     void run_createsAdminWithHashedPassword_whenNotPresent() throws Exception {
//         UserRepository repo = mock(UserRepository.class);
//         when(repo.findByEmail("admin@example.com")).thenReturn(Optional.empty());

//         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//         DataLoader loader = new DataLoader(repo, encoder);

//         loader.run();

//         ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
//         verify(repo).save(captor.capture());
//         User saved = captor.getValue();

//         assertThat(saved.getEmail()).isEqualTo("admin@example.com");
//         assertThat(saved.getPassword()).isNotEqualTo("password123");
//         assertThat(saved.getPassword()).matches("^\\$2[aby]\\$\\d{2}\\$.*");
//     }
// }