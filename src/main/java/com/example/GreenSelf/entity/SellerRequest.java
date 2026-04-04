package com.example.GreenSelf.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seller_requests")
public class SellerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String message;
    @OneToOne
     User user;
    @Enumerated(EnumType.STRING)
    SellerRequestStatus sellerRequestStatus;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SellerRequestStatus getSellerRequestStatus() {
        return sellerRequestStatus;
    }

    public void setSellerRequestStatus(SellerRequestStatus sellerRequestStatus) {
        this.sellerRequestStatus = sellerRequestStatus;
    }
}
