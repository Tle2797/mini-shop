package com.example.minishop.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** ดัก exception จากทุก Controller แล้วแปลงเป็น JSON รูปแบบเดียวกัน */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404: หาไม่เจอ
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        return Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage());
    }

    // 400: ข้อมูลไม่ผ่าน @Valid → บอกทีละ field ว่าผิดอะไร
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.putIfAbsent(err.getField(), err.getDefaultMessage()));
        return Map.of(
                "status", 400,
                "error", "Validation Failed",
                "errors", fieldErrors);
    }

    // 400: JSON ผิดรูปแบบ เช่น ลืมปิด } หรือส่ง "abc" ในช่องราคา
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadJson(HttpMessageNotReadableException ex) {
        return Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "รูปแบบ JSON ไม่ถูกต้อง");
    }

    // 409: ข้อมูลซ้ำ (เช่น username ซ้ำ)
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(ConflictException ex) {
        return Map.of("status", 409, "error", "Conflict", "message", ex.getMessage());
    }

    // 409: กันกรณี 2 คนสมัครชื่อเดียวกันพร้อมกัน → ชน UNIQUE ใน DB
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDuplicateKey(DuplicateKeyException ex) {
        return Map.of("status", 409, "error", "Conflict", "message", "ข้อมูลนี้มีอยู่แล้ว");
    }

    // 401: login ไม่ผ่าน
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        return Map.of("status", 401, "error", "Unauthorized", "message", ex.getMessage());
    }

    // 400: ชนิดข้อมูลใน URL ผิด เช่น GET /api/products/abc (id ต้องเป็นตัวเลข)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return Map.of("status", 400, "error", "Bad Request",
                "message", "ค่า " + ex.getName() + " ไม่ถูกต้อง");
    }

    // 404: เรียก URL ที่ไม่มีอยู่ เช่น GET /api/nope
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNoResource(NoResourceFoundException ex) {
        return Map.of("status", 404, "error", "Not Found", "message", "ไม่พบ URL นี้");
    }

    // 500: ตาข่ายสุดท้าย — error ที่ไม่ได้คาดไว้ทั้งหมด
    // ไม่ส่งรายละเอียดภายในให้ client (อาจรั่วข้อมูลระบบ) แต่ log ไว้ดูฝั่ง backend
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return Map.of("status", 500, "error", "Internal Server Error",
                "message", "เกิดข้อผิดพลาดในระบบ กรุณาลองใหม่อีกครั้ง");
    }
}