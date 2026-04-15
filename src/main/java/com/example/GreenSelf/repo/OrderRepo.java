package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.entity.Nursery;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
    List<Order> findByNursery(Nursery nursery);
    
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o")
    Double getTotalRevenue();
}