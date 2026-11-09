package com.example.minishop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.minishop.dto.ProductRequest;
import com.example.minishop.model.Product;

@Repository // บอก Spring ว่า class นี้คือชั้นที่คุยกับ DB → สร้าง object ให้อัตโนมัติ
            // (Bean)
public class ProductRepository {

    // คอลัมน์ที่ SELECT ทุกครั้ง (เขียนครั้งเดียว ใช้ซ้ำ)
    private static final String COLUMNS = "id, name ,description, price, stock, created_at, updated_at";

    /**
     * RowMapper = วิธีแปลง "1 แถวจาก ResultSet" → "1 object Product"
     * rs = ResultSet (ผลลัพธ์จาก DB), rowNum = ลำดับแถว (ไม่ได้ใช้)
     */
    private static final RowMapper<Product> ROW_MAPPER = (rs, rowNum) -> new Product(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getBigDecimal("price"),
            rs.getInt("stock"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class));

    private final JdbcTemplate jdbcTemplate;

    // Constructor injection: Spring ส่ง JdbcTemplate (ที่ต่อ DB ตาม
    // application.properties) มาให้

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** SELECT ทั้งหมด หรือค้นหาด้วยชื่อถ้ามี keyword */
    public List<Product> findAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            String sql = "SELECT " + COLUMNS + " FROM products ORDER BY id";
            return jdbcTemplate.query(sql, ROW_MAPPER);
        }
        // ⚠️ ใช้ ? แล้วส่งค่าแยก ห้ามเอา keyword ไปต่อ string ตรงๆ (กัน SQL Injection)
        String sql = "SELECT " + COLUMNS + " FROM products WHERE name ILIKE ? ORDER BY id";
        return jdbcTemplate.query(sql, ROW_MAPPER, "%" + keyword.trim() + "%");
    }

    /** SELECT ทีละตัว: ไม่เจอ → Optional.empty() */
    public Optional<Product> findById(Long id) {
        String sql = "SELECT " + COLUMNS + " FROM products WHERE id = ?";
        return jdbcTemplate.query(sql, ROW_MAPPER, id).stream().findFirst();
    }

    /**
     * INSERT แล้วใช้ RETURNING ของ PostgreSQL
     * ดึงแถวที่เพิ่งสร้างกลับมาในคำสั่งเดียว
     */
    public Product create(ProductRequest req) {
        String sql = """
                INSERT INTO products (name, description, price, stock)
                VALUES (?, ?, ?, ?)
                RETURNING %s
                """.formatted(COLUMNS);
        return jdbcTemplate.queryForObject(sql, ROW_MAPPER,
                req.name().trim(), req.description(), req.price(), req.stock());
    }

    /** UPDATE + ตั้ง updated_at = เวลาปัจจุบัน; ไม่เจอ id → Optional.empty() */
    public Optional<Product> update(Long id, ProductRequest req) {
        String sql = """
                UPDATE products
                SET name = ?, description = ?, price = ?, stock = ?, updated_at = now()
                WHERE id = ?
                RETURNING %s
                """.formatted(COLUMNS);
        return jdbcTemplate.query(sql, ROW_MAPPER,
                req.name().trim(), req.description(), req.price(), req.stock(), id)
                .stream().findFirst();
    }

    /** DELETE: คืน true ถ้าลบได้ (มีแถวถูกลบ ≥ 1) */
    public boolean deleteById(Long id) {
        int affectedRows = jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
        return affectedRows > 0;
    }

}
