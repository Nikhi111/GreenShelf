package com.example.GreenSelf.service;
import com.example.GreenSelf.Dto.NurseryDto;
import com.example.GreenSelf.Dto.SellerDto;
import com.example.GreenSelf.Dto.SellerRequestDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.Nursery;
import com.example.GreenSelf.entity.Role;
import com.example.GreenSelf.entity.Seller;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.repo.NurseryRepo;
import com.example.GreenSelf.repo.SellerRepo;
import com.example.GreenSelf.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    SellerRepo sellerRepo;
    @Autowired
    NurseryRepo nurseryRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Transactional
    public String verifyRequestSeller(SellerRequestDto sellerRequestDto){
        System.out.println("entered the verifed seller");
      Seller seller=sellerRepo.findById(sellerRequestDto.getId()).orElseThrow(()->new RuntimeException("Seller not found"));
      seller.setApprovedSeller(true);
        System.out.println("set verifed seller");
      sellerRepo.save(seller);
      return (seller.getApprovedSeller())?"verfied true":"verified false";
    }
    @Transactional
    public String verifyNusery(String username, int nurseryId) {
       User user =userRepo.findUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"no such user exist"));
       if (user.getRole()!=Role.ADMIN) new ResponseStatusException(HttpStatus.FORBIDDEN,"this role dont have access to this url");
       Nursery nursery=nurseryRepo.findById(nurseryId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"no such user exist"));
       nursery.setVerified(true);
       nurseryRepo.save(nursery);
       return "Verified Successfully";
    }
    @Transactional
    public User createAdmin(UserRequestDto userRequestDto) {
        Optional<User> data=userRepo.findUserByUsername(userRequestDto.getUsername());
        User user=new User();
        if (data.isPresent()) throw new RuntimeException("User Already exist"+user.getUsername());
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.ADMIN);
       return userRepo.save(user);
    }

    public Page<NurseryDto> findAllNursery(String username, Pageable pageable) {
        User user =userRepo.findUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"no such user exist"));
        if (user.getRole()!=Role.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"this role dont have access to this url");
        Page<Nursery> nurseries=nurseryRepo.findAll(pageable);
        return nurseries.map(nursery -> {
            NurseryDto nurseryDto=new NurseryDto();
                  nurseryDto.setId(nursery.getId());
                  nurseryDto.setLicenceNumber(nursery.getLicenceNumber());
                  nurseryDto.setAddress(nursery.getAddress());
                  nurseryDto.setName(nursery.getName());
                  return nurseryDto;
        });
    }

    public Page<NurseryDto> searchNurseryByName(String username, String name, Pageable pageable) {
        User user = userRepo.findUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"no such user exist"));
        if (user.getRole() != Role.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"this role dont have access to this url");
        Page<Nursery> nurseries = nurseryRepo.findByNameContainingIgnoreCase(name, pageable);
        return nurseries.map(nursery -> {
            NurseryDto nurseryDto = new NurseryDto();
            nurseryDto.setId(nursery.getId());
            nurseryDto.setLicenceNumber(nursery.getLicenceNumber());
            nurseryDto.setAddress(nursery.getAddress());
            nurseryDto.setName(nursery.getName());
            return nurseryDto;
        });
    }


    public Page<SellerDto> findAllseller(String username, Pageable pageable) {
        User user =userRepo.findUserByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"no such user exist"));
        if (user.getRole()!=Role.ADMIN) new ResponseStatusException(HttpStatus.FORBIDDEN,"this role dont have access to this url");
        Page<Seller> sellers=sellerRepo.findAll(pageable);
        return sellers.map(seller -> {
            SellerDto sellerDto=new SellerDto();
            sellerDto.setId(seller.getId());
            sellerDto.setName(seller.getNurseryName());
            sellerDto.setVerified(seller.getApprovedSeller());
            sellerDto.setNurseryName(seller.getNurseryName());
            return sellerDto;
        });
    }


}
