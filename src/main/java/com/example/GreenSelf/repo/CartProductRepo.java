package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductRepo  extends JpaRepository<CartProduct,Integer> {
}
