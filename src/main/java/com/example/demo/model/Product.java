package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    @Column(name = "description_text", columnDefinition = "TEXT")
    private String description;
    
    private double discountPercentage; // % giảm giá (nếu có thì hiển thị ở mục flash sale)
    
    @Column(name = "promo_quantity")
    private Integer promoQuantity; // Số lượng khuyến mãi
    
    @Column(name = "discount_price")
    private double oldDiscountPrice = 0.0; // Dummy để pass qua cột cũ bị NOT NULL dưới Database
    
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String image; // Added image field for "stunning" UI potential

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getPromoQuantity() {
        return promoQuantity;
    }

    public void setPromoQuantity(Integer promoQuantity) {
        this.promoQuantity = promoQuantity;
    }
}
