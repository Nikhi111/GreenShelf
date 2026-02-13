package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.InventoryProduct;
import com.example.GreenSelf.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface InventoryProductRepo extends JpaRepository<InventoryProduct,Integer>{
    Optional<InventoryProduct> findByProduct(Product product);
}
