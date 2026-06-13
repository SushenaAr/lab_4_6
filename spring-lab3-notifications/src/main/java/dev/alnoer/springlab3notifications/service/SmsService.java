package dev.alnoer.springlab3notifications.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService implements MessageService {
    @Override
    public void sendMessage(String message, String recipient) {
        System.out.println("SMS to " + recipient + ": " + message);
    }
}
