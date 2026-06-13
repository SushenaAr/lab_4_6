package org.example.service;


import org.example.model.dto.RegisterRequest;
import org.example.model.entity.User;
import org.example.model.enums.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthService authService;

    @Test
    void test() {
        //given

        RegisterRequest request = new RegisterRequest("123123", "123123@mas.ru", "password123");
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(UserRole.ROLE_USER);
        user.setCreatedAt(LocalDateTime.now());

        when(passwordEncoder.encode(request.getPassword())).thenReturn(request.getPassword());
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        authService.registerUser(request);

        verify(userRepository, times(1)).save(any());

    }

}
