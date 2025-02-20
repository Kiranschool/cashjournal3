package com.cashjournal.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String home() {
        return "Hello from Cash Journal!";
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
} 