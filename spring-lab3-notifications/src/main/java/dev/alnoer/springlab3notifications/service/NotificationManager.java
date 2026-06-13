package dev.alnoer.springlab3notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationManager {
    private final List<MessageService> messageServices;

    @Autowired
    public NotificationManager(List<MessageService> messageServices) {
        this.messageServices = messageServices;
    }

    public void notify(String message, String recipient) {
        messageServices.forEach( messageService ->
            messageService.sendMessage(message, recipient)
        );
    }
}
