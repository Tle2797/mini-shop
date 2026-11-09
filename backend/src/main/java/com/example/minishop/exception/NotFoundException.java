package com.example.minishop.exception;

/** โยนเมื่อหาข้อมูลไม่เจอ → GlobalExceptionHandler จะแปลงเป็น HTTP 404 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}