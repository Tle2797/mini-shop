// กำหนดความปลอดภัยทั้งหมด
package com.example.minishop.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            // ปิด CSRF: ใช้ JWT ใน header ไม่ได้ใช้ cookie session จึงไม่เสี่ยง CSRF
            .csrf(csrf -> csrf.disable())

            // เปิด CORS โดยใช้ bean corsConfigurationSource() ด้านล่าง
            .cors(Customizer.withDefaults())

            // ไม่สร้าง session: ทุก request ต้องแนบ token มาเอง
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ปิดหน้า login / basic auth ที่ Spring ให้มา
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // กฎว่า URL ไหนใครเข้าได้ (อ่านจากบนลงล่าง ตัวแรกที่ตรงจะถูกใช้)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()            // CORS preflight
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()                                       // ที่เหลือต้อง login
            )

            // ถ้ายังไม่ login แต่เรียก endpoint ที่ต้อง login → ตอบ JSON 401
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"กรุณาเข้าสู่ระบบ\"}");
            }))

            // ใส่ JwtAuthFilter ไว้ก่อน filter login มาตรฐานของ Spring
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** ตัว hash รหัสผ่าน */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** อนุญาตให้ Angular dev server (localhost:4200) เรียก API ได้ */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}