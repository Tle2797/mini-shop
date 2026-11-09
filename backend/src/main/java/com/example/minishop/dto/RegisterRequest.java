package com.example.minishop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "กรุณากรอกชื่อผู้ใช้")
        @Size(min = 3, max = 50, message = "ชื่อผู้ใช้ต้องยาว 3-50 ตัวอักษร")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ชื่อผู้ใช้ใช้ได้เฉพาะ a-z, 0-9 และ _")
        String username,

        @NotBlank(message = "กรุณากรอกรหัสผ่าน")
        @Size(min = 6, max = 72, message = "รหัสผ่านต้องยาว 6-72 ตัวอักษร")
        String password
) {}