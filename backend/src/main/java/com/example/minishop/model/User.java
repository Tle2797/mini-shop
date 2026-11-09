package com.example.minishop.model;

import java.time.LocalDateTime;

public record User(
        Long id,
        String username,
        String passwordHash,   // ⚠️ ห้ามส่ง field นี้ออกไปใน response เด็ดขาด
        LocalDateTime createdAt
) {}