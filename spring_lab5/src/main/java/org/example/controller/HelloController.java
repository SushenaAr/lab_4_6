package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "\uD83D\uDCA5 Привет, Spring Boot! \uD83D\uDCA5";
    }

    @GetMapping("/goodbye")
    public String sayGoodbye() {
        return "\uD83D\uDE2D До свидания, Spring Boot! \uD83D\uDE2D";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return "Привет, " + name + " \uD83D\uDC31";
    }

    @GetMapping("/userinfo")
    public String userInfo(@RequestParam String name, @RequestParam int age) {
        return "⚠\uFE0F" + "Пользователь: " + name + ", возраст: " + age + "⚠\uFE0F";
    }
}