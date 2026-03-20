package com.example.demo;
import lombok.AllArgsConstructor;
@AllArgsConstructor
public enum Role {
 ADMIN(1), // Vai trò quản trị viên, có quyền cao nhất trong hệ thống.
 MANAGER(3), // Vai trò quản lý, hạn chế quyền hơn admin
 USER(2); // Vai trò người dùng bình thường, có quyền hạn giới hạn.
 public final long value; 
}
