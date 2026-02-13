package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo  extends JpaRepository<Address,Integer> {

}
