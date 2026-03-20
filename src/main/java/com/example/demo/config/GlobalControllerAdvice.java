package com.example.demo.config;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Tự động đưa danh mục gốc (có children) vào tất cả các views
     * để layout.html có thể render mega-menu "Phụ kiện"
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        List<Category> parentCategories = categoryRepository.findByParentIsNull();
        model.addAttribute("globalParentCategories", parentCategories);
    }
}
