package com.example.minishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// exclude: ปิด "user ทดสอบ + รหัสสุ่ม" ที่ Spring Security สร้างให้อัตโนมัติ
// เพราะเราใช้ตาราง users + JWT ของเราเอง
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MiniShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniShopApplication.class, args);
    }
}