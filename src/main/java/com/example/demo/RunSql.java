package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RunSql {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/thegioididong2";
        String user = "postgres";
        String password = "1";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("ALTER TABLE order_items ALTER COLUMN product_image TYPE TEXT");
            System.out.println("Altered product_image");

            stmt.executeUpdate("ALTER TABLE order_items ALTER COLUMN product_name TYPE VARCHAR(500)");
            System.out.println("Altered product_name");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
