package com.example.demo.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Long productId;
    private String productName;
    private String productImage;
    private double unitPrice;      // actual price (after discount)
    private int quantity;

    public CartItem() {}

    public CartItem(Long productId, String productName, String productImage,
                    double unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    // --- getters/setters ---
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
