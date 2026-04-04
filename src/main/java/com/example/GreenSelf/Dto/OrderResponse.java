package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    // ── Razorpay fields (frontend needs these to open checkout) ───────────────
    private String razorpayOrderId;
    private String keyId;           // rzp_test_XXXX
    private Double amount;
    private String currency = "INR";

    // ── Order details ─────────────────────────────────────────────────────────
    private Long orderId;
    private OrderStatus orderStatus;
    private Double totalPrice;
    private Double platformCommission;
    private Double sellerAmount;

    // ── User ──────────────────────────────────────────────────────────────────
    private String username;

    // ── Nursery ───────────────────────────────────────────────────────────────
    private String nurseryName;

    // ── Address ───────────────────────────────────────────────────────────────
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private int pincode;

    // ── Products ──────────────────────────────────────────────────────────────
    private List<OrderProductResponse> products;

    // ── Timestamp ─────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;

    // ── Inner class ───────────────────────────────────────────────────────────
    public static class OrderProductResponse {
        private int productId;
        private String productName;
        private int count;
        private int prize;

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public int getPrize() { return prize; }
        public void setPrize(int prize) { this.prize = prize; }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public Double getPlatformCommission() { return platformCommission; }
    public void setPlatformCommission(Double platformCommission) {
        this.platformCommission = platformCommission;
    }

    public Double getSellerAmount() { return sellerAmount; }
    public void setSellerAmount(Double sellerAmount) {
        this.sellerAmount = sellerAmount;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNurseryName() { return nurseryName; }
    public void setNurseryName(String nurseryName) { this.nurseryName = nurseryName; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() { return addressLine3; }
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public int getPincode() { return pincode; }
    public void setPincode(int pincode) { this.pincode = pincode; }

    public List<OrderProductResponse> getProducts() { return products; }
    public void setProducts(List<OrderProductResponse> products) {
        this.products = products;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}