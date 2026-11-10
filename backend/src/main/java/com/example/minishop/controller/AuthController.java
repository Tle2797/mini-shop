package com.example.minishop.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.minishop.dto.AuthResponse;
import com.example.minishop.dto.LoginRequest;
import com.example.minishop.dto.RegisterRequest;
import com.example.minishop.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register → 201
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    // Post /api/auth/login -> 200
    @PostMapping ("/login")
    public  AuthResponse login(@Valid @RequestBody  LoginRequest req) {
        return authService.login(req);
    }

     // GET /api/auth/me → ต้องแนบ token; Spring ส่ง Authentication ที่ JwtAuthFilter ตั้งไว้มาให้
    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("username", authentication.getName());
    }

}
