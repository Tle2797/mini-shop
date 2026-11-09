package com.example.minishop.dto;

/** สิ่งที่ส่งกลับหลัง register/login สำเร็จ */
public record AuthResponse(
        String token,
        String tokenType,   // "Bearer"
        long expiresIn,     // อายุ token (มิลลิวินาที)
        String username
) {}