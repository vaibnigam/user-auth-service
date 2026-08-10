package com.vaibhav.userauthservice.controller;

import com.vaibhav.userauthservice.dto.LoginRequest;
import com.vaibhav.userauthservice.dto.LoginResponse;
import com.vaibhav.userauthservice.dto.RegisterRequest;
import com.vaibhav.userauthservice.entity.User;
import com.vaibhav.userauthservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User savedUser = userService.registerUser(request);
        return ResponseEntity.ok(savedUser);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = userService.loginUser(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("Logged in as: " + email);
    }
}