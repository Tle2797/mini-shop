package com.example.minishop.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.minishop.model.User;

/**
 * Repository สำหรับตาราง "users" — เป็นชั้นเดียวที่คุยกับฐานข้อมูลโดยตรง
 * (Service/Controller เรียกผ่านคลาสนี้ ไม่เขียน SQL เอง)
 *
 * @Repository = บอก Spring ให้สร้าง instance ของคลาสนี้ไว้เป็น bean
 * เพื่อให้ฉีด (inject) ไปใช้ที่อื่นได้
 */
@Repository
public class UserRepository {

    // ชื่อคอลัมน์ที่ใช้ซ้ำในทุก query เก็บไว้ที่เดียว จะได้ไม่พิมพ์ซ้ำและไม่ลืมคอลัมน์
    private static final String COLUMNS = "id, username, password_hash, created_at";

    /**
     * RowMapper = ตัวแปลง "แถวจาก ResultSet" -> "object User" ทีละแถว
     * (rs = ผลลัพธ์ของแถวปัจจุบัน, rowNum = ลำดับแถว ซึ่งเราไม่ได้ใช้)
     * ชื่อใน rs.getXxx("...") ต้องตรงกับชื่อคอลัมน์ในตาราง
     * ประกาศเป็น static final เพราะไม่มี state จึงใช้ร่วมกันทุก query ได้
     */
    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            // แปลง timestamp ในฐานข้อมูลเป็น LocalDateTime ของ Java
            rs.getObject("created_at", LocalDateTime.class)
    );

    // JdbcTemplate = เครื่องมือของ Spring สำหรับรัน SQL (จัดการ connection/ปิดทรัพยากรให้เอง)
    private final JdbcTemplate jdbcTemplate;

    // Constructor injection: Spring ส่ง JdbcTemplate เข้ามาให้อัตโนมัติ
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * ค้นหา user จาก username (ใช้ตอน login)
     * คืนค่าเป็น Optional: ถ้าไม่เจอจะเป็น Optional.empty() แทนที่จะเป็น null
     */
    public Optional<User> findByUsername(String username) {
        // "?" คือ placeholder — ค่า username ถูกส่งแยกต่างหาก ป้องกัน SQL Injection
        String sql = "SELECT " + COLUMNS + " FROM users WHERE username = ?";
        // query() คืน List ของ User -> เอาตัวแรก (ถ้ามี) ห่อเป็น Optional
        return jdbcTemplate.query(sql, ROW_MAPPER, username).stream().findFirst();
    }

    /**
     * เช็คว่ามี username นี้อยู่แล้วหรือยัง (ใช้ตอน register เพื่อกันชื่อซ้ำ)
     */
    public boolean existsByUsername(String username) {
        // EXISTS หยุดทันทีที่เจอแถวแรก เร็วกว่า COUNT(*) และได้ค่า true/false ตรงๆ
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE username = ?)";
        // Boolean.TRUE.equals(...) ป้องกัน NullPointerException กรณีค่าที่ได้เป็น null
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, username));
    }

    /**
     * สร้าง user ใหม่ แล้วคืน User ที่เพิ่งสร้าง (มี id และ created_at ที่ DB เป็นคนสร้างให้)
     * รับ passwordHash (รหัสผ่านที่ hash แล้ว) ไม่ใช่รหัสผ่านจริง
     */
    public User create(String username, String passwordHash) {
        // Text block (""") เขียน SQL หลายบรรทัดได้อ่านง่าย
        // RETURNING (เฉพาะ PostgreSQL) = INSERT แล้วส่งแถวที่เพิ่งเพิ่มกลับมาเลย
        // ไม่ต้อง SELECT ซ้ำอีกรอบ; %s ถูกแทนด้วยรายชื่อคอลัมน์ผ่าน .formatted()
        String sql = """
                INSERT INTO users (username, password_hash)
                VALUES (?, ?)
                RETURNING %s
                """.formatted(COLUMNS);
        // ใช้ queryForObject เพราะ INSERT ... RETURNING ให้ผลลัพธ์เป็น 1 แถว
        return jdbcTemplate.queryForObject(sql, ROW_MAPPER, username, passwordHash);
    }
}
