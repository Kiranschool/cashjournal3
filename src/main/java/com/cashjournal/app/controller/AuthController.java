package com.cashjournal.app.controller;

import com.cashjournal.app.dto.LoginRequest;
import com.cashjournal.app.dto.SignupRequest;
import com.cashjournal.app.model.User;
import com.cashjournal.app.service.UserService;
import com.cashjournal.app.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String root(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() ? "redirect:/home" : "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() ? "redirect:/home" : "login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() ? "redirect:/home" : "signup";
    }

    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.registerUser(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getFirstName()
            );
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User registered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        model.addAttribute("firstName", userService.findByUsername(username).getFirstName());
        return "home";
    }

    @GetMapping("/finance-calendar-journal")
    public String financeCalendarJournal(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String userId = authentication.getName();
        model.addAttribute("balance", transactionService.calculateBalance(userId));
        model.addAttribute("transactions", transactionService.getUserTransactions(userId));
        return "finance-calendar-journal";
    }

    @PostMapping("/api/auth/update-password")
    @ResponseBody
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        try {
            String usernameOrEmail = request.get("usernameOrEmail");
            String newPassword = request.get("password");
            userService.updatePassword(usernameOrEmail, newPassword);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/wishlist")
    public String showWishlistPage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        return "wishlist";
    }

    @GetMapping("/spending-tracker")
    public String showSpendingTrackerPage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        return "spending-tracker";
    }

    @GetMapping("/account")
    public String showAccountPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "account";
    }
} 