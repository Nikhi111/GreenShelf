package com.example.GreenSelf.service;

import com.example.GreenSelf.entity.Order;
import com.example.GreenSelf.entity.OrderStatus;
import com.example.GreenSelf.repo.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
@Service
public class PayoutService {
    @Autowired
    private OrderRepo orderRepo;

    @Transactional
    public String processPayout(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Order not delivered yet — cannot payout");
        }

        // Simulated payout — replace with real Razorpay API when going live
        String mockPayoutId = "pout_mock_" + System.currentTimeMillis();

        order.setOrderStatus(OrderStatus.SETTLED);
        order.setSettledAt(LocalDateTime.now());
        orderRepo.save(order);

        return mockPayoutId;
    }
}