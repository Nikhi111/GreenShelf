package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankDetailsRepo extends JpaRepository<BankDetails, Integer> {

    Optional<BankDetails> findBySellerIdAndIsPrimaryTrue(int sellerId);

}