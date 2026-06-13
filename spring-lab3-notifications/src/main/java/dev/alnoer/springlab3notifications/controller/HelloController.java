package dev.alnoer.springlab3notifications.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, spring boot! but FastAPI from python better";
    }

    @GetMapping("/goodbye")
    public String sayGoodbye() {
        return "Goodbye, spring boot! you no this";
    }

    @GetMapping("/greet")
    public String sayGoodbye(@RequestParam String name) {
        return "Hi, " + name;
    }
}
