package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface   ProductRepo  extends JpaRepository<Product,Integer> {

    Page<Product> findByNameContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

}
