package com.example.minishop.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** ดัก exception จากทุก Controller แล้วแปลงเป็น JSON รูปแบบเดียวกัน */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: หาไม่เจอ
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        return Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        );
    }

    // 400: ข้อมูลไม่ผ่าน @Valid → บอกทีละ field ว่าผิดอะไร
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage()));
        return Map.of(
                "status", 400,
                "error", "Validation Failed",
                "errors", fieldErrors
        );
    }

    // 400: JSON ผิดรูปแบบ เช่น ลืมปิด } หรือส่ง "abc" ในช่องราคา
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadJson(HttpMessageNotReadableException ex) {
        return Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "รูปแบบ JSON ไม่ถูกต้อง"
        );
    }
}