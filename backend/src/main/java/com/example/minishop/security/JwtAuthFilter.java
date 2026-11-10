package com.example.minishop.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// ด่านตรวจที่ทุก request ต้องผ่าน
/**
 * ทำงาน 1 ครั้งต่อ request:
 * อ่าน header "Authorization: Bearer <token>" → ถ้า token ถูกต้อง บอก Spring ว่า user นี้ login แล้ว
 *
 * ⚠️ ไม่ใส่ @Component เพราะถ้าใส่ Spring Boot จะลงทะเบียน filter นี้ซ้ำ 2 ที่
 *    เราจะสร้างมันเองใน SecurityConfig แทน
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                String username = jwtService.extractUsername(token);

                // สร้าง "บัตรผ่าน" ใส่ไว้ใน SecurityContext (constructor 3 ตัว = authenticated แล้ว)
                var authentication = new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // token ผิด/หมดอายุ → ไม่ตั้งค่า login → ถ้า endpoint ต้อง login จะได้ 401
                SecurityContextHolder.clearContext();
            }
        }

        // ส่งต่อให้ filter ตัวถัดไป (สุดท้ายคือ Controller)
        filterChain.doFilter(request, response);
    }
}