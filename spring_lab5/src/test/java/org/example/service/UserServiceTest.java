package org.example.service;

import org.example.model.dto.UserDto;
import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        //given
        UserDto dto = UserDto.builder()
                .name("Иван Иванов")
                .email("ivan@example.com")
                .phone("+79990001122")
                .deviceToken("device-token-123")
                .telegramChatId("123456789")
                .build();
        User savedUser = new User();
        savedUser.setName(dto.getName());
        savedUser.setEmail(dto.getEmail());
        savedUser.setPhone(dto.getPhone());
        savedUser.setDeviceToken(dto.getDeviceToken());
        savedUser.setTelegramChatId(dto.getTelegramChatId());
        savedUser.setPassword("password123");
        when(passwordEncoder.encode("password123")).thenReturn("password123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        //when
        User result = userService.createUser(dto);

        //then
        assertNotNull(result);
        assertEquals("Иван Иванов", result.getName());
        assertEquals("ivan@example.com", result.getEmail());
    }

    @Test
    void shouldFindUser() {
        //given
        User expectedResult = User.builder().id(11L).build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(expectedResult));
        //when
        User actualResult = userRepository.findById(11L).get();
        //then
        assertThat(expectedResult).isEqualTo(actualResult);
    }

    @Test
    void shouldDelete() {
        //given
        User user = User.builder().id(11L).build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        //when
        userService.deleteUser(11L);

        //then
        verify(userRepository).delete(user);
    }

    @Test
    void shouldCallSaveOnRepository() {
        UserDto dto = UserDto.builder()
                .name("Иван")
                .email("ivan@example.com")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(new User());
        userService.createUser(dto);
        verify(userRepository).save(any(User.class));
    }

}