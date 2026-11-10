package com.example.minishop.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// ใช้สร้างและอ่าน token
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    // @Value ดึงค่าจาก application.properties
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    /** สร้าง token: ใส่ username (subject) + เวลาออก + เวลาหมดอายุ แล้วเซ็นด้วย key */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * อ่าน username จาก token
     * ถ้าลายเซ็นผิด / หมดอายุ / รูปแบบผิด → JJWT จะ throw JwtException
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}