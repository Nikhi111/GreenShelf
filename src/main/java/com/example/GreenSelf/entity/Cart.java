package com.example.GreenSelf.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CartProduct> cartproductList;
    @ManyToOne
    User user;
    int totalPrize;
    @Enumerated(EnumType.STRING)
    CartStatus cartStatus;
    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CartProduct> getCartproductList() {
        return cartproductList;
    }

    public void setCartproductList(List<CartProduct> cartproductList) {
        this.cartproductList = cartproductList;
    }

    public int getTotalPrize() {
        return totalPrize;
    }
    public void addCartProduct(CartProduct cartProduct) {
        if (cartproductList == null) {
            cartproductList = new ArrayList<>();
        }
        cartproductList.add(cartProduct);
        cartProduct.setCart(this);
    }
    public void removeCartProduct(CartProduct cartProduct) {
        if (cartproductList != null) {
            cartproductList.remove(cartProduct);
        }
    }

    public void setTotalPrize(int totalPrize) {
        this.totalPrize = totalPrize;
    }

    public CartStatus getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(CartStatus cartStatus) {
        this.cartStatus = cartStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
