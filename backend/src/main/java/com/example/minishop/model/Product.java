package com.example.minishop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * แทนข้อมูล 1 แถวในตาราง products
 * ใช้ record (Java 16+) = class ที่เก็บข้อมูลอย่างเดียว แก้ค่าไม่ได้
 * Java สร้าง constructor, getter (เช่น name()), equals, toString ให้อัตโนมัติ
 */
public record Product(
        Long id,
        String name,
        String description,
        BigDecimal price,        // เงินใช้ BigDecimal เสมอ (คู่กับ NUMERIC ใน DB)
        Integer stock,
        LocalDateTime createdAt, // คอลัมน์ created_at → field createdAt
        LocalDateTime updatedAt
) {}