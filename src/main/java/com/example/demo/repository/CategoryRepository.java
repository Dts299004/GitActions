package com.example.demo.repository;

import com.example.demo.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy tất cả danh mục gốc (không có cha)
    List<Category> findByParentIsNull();

    // Lấy danh mục con theo cha
    List<Category> findByParent(Category parent);

    // Lấy danh mục gốc theo tên (tìm kiếm danh mục phụ kiện)
    Category findByNameIgnoreCaseAndParentIsNull(String name);

    // Lấy tất cả danh mục con (có cha)
    List<Category> findByParentIsNotNull();
}
