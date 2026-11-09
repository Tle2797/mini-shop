package com.example.minishop.exception;

// ใช้กับ 409 เช่น username ซ้ำ
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}