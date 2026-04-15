package com.example.GreenSelf.repo;

import com.example.GreenSelf.Dto.SellerRequestDto;
import com.example.GreenSelf.entity.Seller;
import com.example.GreenSelf.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {
    Optional<Seller> findByUserAndIsApprovedSeller(User user, Boolean isApprovedSeller);
    Optional<Seller> findByUser(User user);
    Page<Seller> findByUserUsernameContainingIgnoreCase(String username, Pageable pageable);
    long countByIsApprovedSellerFalse();
}
