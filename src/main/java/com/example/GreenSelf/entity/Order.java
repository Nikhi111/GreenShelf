package com.example.GreenSelf.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // ✅ FIXED: int → Long

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts;

    private Double totalPrice;              // ✅ FIXED: int → Double

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private Nursery nursery;               // ✅ your seller = nursery

    @OneToOne
    private Address address;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;       // keep your existing enum

    // ── Payment fields ADDED ──────────────────────────────────────────────────

    private String razorpayOrderId;        // from Razorpay when order created
    private String razorpayPaymentId;      // from Razorpay after customer pays
    private String razorpaySignature;      // for verification

    private Double platformCommission;     // platform's cut
    private Double sellerAmount;           // nursery receives this

    private LocalDateTime paidAt;          // when payment was done
    private LocalDateTime settledAt;       // when payout was sent to nursery

    // ── Auto timestamps ───────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<OrderProduct> getOrderProducts() { return orderProducts; }
    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Nursery getNursery() { return nursery; }
    public void setNursery(Nursery nursery) { this.nursery = nursery; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    public Double getPlatformCommission() { return platformCommission; }
    public void setPlatformCommission(Double platformCommission) {
        this.platformCommission = platformCommission;
    }

    public Double getSellerAmount() { return sellerAmount; }
    public void setSellerAmount(Double sellerAmount) {
        this.sellerAmount = sellerAmount;
    }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getSettledAt() { return settledAt; }
    public void setSettledAt(LocalDateTime settledAt) { this.settledAt = settledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}