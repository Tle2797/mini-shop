package com.example.minishop.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// คือรูปแบบข้อมูลที่ client ส่งเข้ามา แยกจาก Model เพราะ client ไม่ควรส่ง id, createdAt มาเองได้

/** ข้อมูลที่ใช้ตอนสร้าง (POST) และแก้ไข (PUT) สินค้า */
public record ProductRequest (
    @NotBlank (message = "กรุณากรอกชื่อสินค้า")
    @Size (max = 150, message = "ชื่อสินค้ายาวได้ไม่เกิน 150 ตัวอักษร")
    String name,

    @Size (max = 1000, message = "รายละเอียดยาวได้ไม่เกิน 1000 ตัวอักษร")
    String description,

    @NotNull (message = "กรุณากรอกราคา")
    @DecimalMin (value = "0.00", message = "ราคาต้องไม่ติดลบ")
    @Digits (integer = 8, fraction = 2, message = "รานคาต้องมีทศนิยม")
    BigDecimal price,

    @NotNull (message = "กรุณากรอกจำนวนสต๊อก")
    @Min (value = 0, message = "สต๊อกต้องไม่ติดลบ")
    Integer stock
){}
