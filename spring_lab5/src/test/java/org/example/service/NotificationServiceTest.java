package org.example.service;

import org.example.exception.ResourceNotFoundException;
import org.example.model.dto.NotificationDto;
import org.example.model.entity.Notification;
import org.example.model.entity.User;
import org.example.model.enums.NotificationChannel;
import org.example.repository.NotificationRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCreateNotification() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("ivan@example.com");
        NotificationDto dto = NotificationDto.builder()
                .title("Напоминание")
                .message("Завтра пара по Spring")
                .channel(NotificationChannel.EMAIL)
                .recipientId(1L)
                .build();
        Notification savedNotification = new Notification();
        savedNotification.setTitle(dto.getTitle());
        savedNotification.setMessage(dto.getMessage());
        savedNotification.setChannel(dto.getChannel());
        savedNotification.setRecipient(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        //when
        Notification result = notificationService.createNotification(dto);

        //then
        assertNotNull(result);
        assertEquals("Напоминание", result.getTitle());
        assertEquals(NotificationChannel.EMAIL, result.getChannel());
        assertEquals(user, result.getRecipient());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        NotificationDto dto = NotificationDto.builder()
                .title("Напоминание")
                .message("Сообщение")
                .channel(NotificationChannel.EMAIL)
                .recipientId(99L)
                .build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //when & then
        assertThrows(RuntimeException.class, () ->
                notificationService.createNotification(dto));
    }

    @Test
    void shouldOKWhenNotificationFound() {
        //given
        Notification notification = Notification.builder().id(99L).build();
        when(notificationRepository.findById(99L)).thenReturn(Optional.of(notification));

        //when
        Notification actualResult = notificationService.getNotificationById(notification.getId());

        //then
        assertThat(notification).isEqualTo(actualResult);
    }

    @Test
    void shouldThrowWhenNotificationNotFound() {
        //given
        Notification notification = Notification.builder().id(99L).build();
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.empty());

        //when & then
        assertThrows(ResourceNotFoundException.class,
                () -> notificationService.getNotificationById(notification.getId()));
    }
}