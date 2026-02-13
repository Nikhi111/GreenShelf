package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Nursery;
import com.example.GreenSelf.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface NurseryRepo extends JpaRepository<Nursery, Integer> {
    Optional<Nursery> findByIdAndOwnerAndIsVerified(
            int id,
            Seller owner,
            Boolean isVerified
    );
    Page<Nursery> findByOwner(Seller owner);

}
