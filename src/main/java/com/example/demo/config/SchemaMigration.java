package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Tự động sửa kiểu cột khi ứng dụng khởi động.
 * Hibernate update không tự alter kiểu cột đã tồn tại.
 */
@Component
public class SchemaMigration implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE order_items ALTER COLUMN product_image TYPE TEXT"
            );
            System.out.println("[Migration] Đã đổi product_image -> TEXT");
        } catch (Exception e) {
            // Bỏ qua nếu đã đúng kiểu hoặc cột chưa tồn tại
        }

        try {
            jdbcTemplate.execute(
                "ALTER TABLE order_items ALTER COLUMN product_name TYPE VARCHAR(500)"
            );
            System.out.println("[Migration] Đã đổi product_name -> VARCHAR(500)");
        } catch (Exception e) {
            // Bỏ qua nếu đã đúng kiểu
        }
        
        try {
            jdbcTemplate.execute(
                "ALTER TABLE orders ALTER COLUMN order_id TYPE VARCHAR(500)"
            );
        } catch (Exception e) { /* bỏ qua */ }
    }
}
