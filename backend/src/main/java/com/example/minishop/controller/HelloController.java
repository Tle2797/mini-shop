package com.example.minishop.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController             // class นี้คืน JSON (ไม่ใช่หน้า HTML)

@RequestMapping("/api") // ทุก endpoint ใน class นี้ขึ้นต้นด้วย /api
public class HelloController {
    @GetMapping("/hello")
    public Map<String, Object> hello() {
         // Spring แปลง Map เป็น JSON ให้อัตโนมัติ (ผ่าน Jackson)
        return Map.of(
            "message", "mini Shop API is running",
            "time", LocalDateTime.now().toString()
        );

    }
}
