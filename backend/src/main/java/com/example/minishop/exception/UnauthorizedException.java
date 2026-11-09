package com.example.minishop.exception;

// ใช้กับ 401 เช่น login ผิด
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}