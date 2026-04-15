package com.fitapp.fitappbackend.controller;

import com.fitapp.fitappbackend.dto.AuthResponse;
import com.fitapp.fitappbackend.dto.LoginRequest;
import com.fitapp.fitappbackend.dto.RegisterRequest;
import com.fitapp.fitappbackend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}