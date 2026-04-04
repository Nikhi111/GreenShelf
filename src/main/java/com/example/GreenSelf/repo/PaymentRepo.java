package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);
}