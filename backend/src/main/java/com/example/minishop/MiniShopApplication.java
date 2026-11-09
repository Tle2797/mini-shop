package com.example.minishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = เปิด auto-configuration + สแกนหา class ใน package นี้และ package ย่อยทั้งหมด
@SpringBootApplication
public class MiniShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniShopApplication.class, args);
    }
}