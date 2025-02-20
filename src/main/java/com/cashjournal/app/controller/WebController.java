package com.cashjournal.app.controller;

import com.cashjournal.app.service.UserService;
import com.cashjournal.app.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class WebController {
    
    private final UserService userService;
    
    // Removing all conflicting mappings since they are handled by AuthController
    // The WebController can be removed if it has no other mappings
} 