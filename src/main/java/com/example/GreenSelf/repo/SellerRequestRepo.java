package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.SellerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface SellerRequestRepo  extends JpaRepository<SellerRequest,Integer>{

}
