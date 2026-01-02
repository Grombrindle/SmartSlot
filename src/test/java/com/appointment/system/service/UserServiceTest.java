// package com.appointment.system.service;

// import com.appointment.system.model.User;
// import com.appointment.system.repository.UserRepository;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mockito;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.Mockito.*;

// class UserServiceTest {

//     @Test
//     void createUser_encodesPasswordBeforeSaving() {
//         UserRepository repo = mock(UserRepository.class);
//         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//         UserServiceImpl service = new UserServiceImpl(repo, encoder);

//         User input = new User();
//         input.setName("Alice");
//         input.setEmail("alice@example.com");
//         input.setPassword("mysecret");

//         when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         User saved = service.createUser(input);

//         ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
//         verify(repo).save(captor.capture());
//         User passed = captor.getValue();

//         assertThat(passed.getPassword()).isNotEqualTo("mysecret");
//         assertThat(passed.getPassword()).matches("^\\$2[aby]\\$\\d{2}\\$.*");
//     }
// }