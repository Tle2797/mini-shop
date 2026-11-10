package com.example.minishop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.minishop.dto.AuthResponse;
import com.example.minishop.dto.LoginRequest;
import com.example.minishop.dto.RegisterRequest;
import com.example.minishop.exception.ConflictException;
import com.example.minishop.exception.UnauthorizedException;
import com.example.minishop.model.User;
import com.example.minishop.repository.UserRepository;
import com.example.minishop.security.JwtService;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ตอน register
    public AuthResponse register(RegisterRequest req) {
        String username = normalize(req.username());

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("ชื่อผู้ใช้นี้ถูกใช้ไปแล้ว");
        }

        // hash ก่อนเก็บเสมอ! ห้ามเก็บ req.password() ลง DB ตรงๆ
        String hash = passwordEncoder.encode(req.password());
        User user = userRepository.create(username, hash);
        return buildResponse(user.username()); // สมัครเสร็จ = login ให้เลย

    }

    public AuthResponse login(LoginRequest req) {
        String username = normalize(req.username());

        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(req.password(), u.passwordHash()))
                // ⚠️ ข้อความเดียวกันทั้ง "ไม่มี user" และ "รหัสผิด" → ไม่บอกใบ้ผู้ไม่หวังดีว่า
                // username ไหนมีอยู่จริง
                .orElseThrow(() -> new UnauthorizedException("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง"));

        return buildResponse(user.username());
    }

    private AuthResponse buildResponse(String username) {
        String token = jwtService.generateToken(username);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(), username);
    }

    private String normalize(String username) {
        return username.trim().toLowerCase();
    }
}
