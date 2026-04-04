package com.example.GreenSelf.entity;

public enum OrderStatus {
    CREATED,       // order placed
    PAID,          // payment done
    CONFIRMED,     // nursery confirmed
    DELIVERED,     // delivered to customer
    SETTLED,       // payout sent to nursery
    CANCELLED,     // cancelled
    FAILED
}
