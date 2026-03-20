package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(Model model) {
        java.util.List<Product> allProducts = productRepository.findAll();
        java.util.List<Product> discountedProducts = allProducts.stream().filter(p -> p.getDiscountPercentage() > 0).toList();
        java.util.List<Product> normalProducts = allProducts.stream().filter(p -> p.getDiscountPercentage() <= 0).toList();
        
        model.addAttribute("discountedProducts", discountedProducts);
        model.addAttribute("normalProducts", normalProducts);
        model.addAttribute("products", allProducts);
        return "product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findByParentIsNotNull());
        return "product-form";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, 
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
                             @RequestParam(value = "imageUrl", required = false) String imageUrl,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findByParentIsNotNull());
            return "product-form";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            product.setImage(imageUrl);
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findByParentIsNotNull());
        return "product-form";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid Product product, BindingResult result, 
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, 
                                @RequestParam(value = "imageUrl", required = false) String imageUrl,
                                Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryRepository.findByParentIsNotNull());
            return "product-form";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path path = Paths.get("src/main/resources/static/images/" + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
                product.setImage("/images/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
             product.setImage(imageUrl);
        } else {
             // Keep existing image if no new file or URL provided
             Product existingProduct = productRepository.findById(id).orElse(null);
             if (existingProduct != null && existingProduct.getImage() != null) {
                 product.setImage(existingProduct.getImage());
             }
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        productRepository.delete(product);
        return "redirect:/products";
    }
}
