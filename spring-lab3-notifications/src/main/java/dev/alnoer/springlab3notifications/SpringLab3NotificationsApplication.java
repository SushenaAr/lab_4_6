package dev.alnoer.springlab3notifications;

import dev.alnoer.springlab3notifications.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Import(AppConfig.class)
public class SpringLab3NotificationsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLab3NotificationsApplication.class, args);
    }
}
