package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Cart;
import com.example.GreenSelf.entity.CartStatus;
import com.example.GreenSelf.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CartRepo extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserAndCartStatus(User user , CartStatus cartStatus);
}