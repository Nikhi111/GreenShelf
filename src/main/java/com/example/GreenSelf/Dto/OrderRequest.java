package com.example.GreenSelf.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {

    // Who is placing the order
    @NotNull(message = "User ID is required")
    private int userId;

    // Which nursery they are ordering from
    @NotNull(message = "Nursery ID is required")
    private int nurseryId;

    // Delivery address (must belong to the user)
    @NotNull(message = "Address ID is required")
    private int addressId;

    // List of products with quantity
    @NotEmpty(message = "At least one product is required")
    private List<OrderProductRequest> products;

    // ── Inner class for each product in order ─────────────────────────────────
    public static class OrderProductRequest {

        @NotNull(message = "Product ID is required")
        private int productId;

        @NotNull(message = "Count is required")
        private int count;

        // Getters & Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getNurseryId() { return nurseryId; }
    public void setNurseryId(int nurseryId) { this.nurseryId = nurseryId; }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    public List<OrderProductRequest> getProducts() { return products; }
    public void setProducts(List<OrderProductRequest> products) {
        this.products = products;
    }
}