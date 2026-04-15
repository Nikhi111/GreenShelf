package com.example.GreenSelf.Dto;

public class CartItemDto {
    private int productId;
    private String productName;
    private String productImage;
    private String nurseryName;
    private int prize;
    private int count;
    private int quantity;

    // Constructors
    public CartItemDto() {}

    public CartItemDto(int productId, String productName, String productImage, String nurseryName, int prize, int count) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.nurseryName = nurseryName;
        this.prize = prize;
        this.count = count;
        this.quantity = count; // Both count and quantity for frontend compatibility
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getNurseryName() {
        return nurseryName;
    }

    public void setNurseryName(String nurseryName) {
        this.nurseryName = nurseryName;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
