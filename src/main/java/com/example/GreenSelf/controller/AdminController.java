package com.example.GreenSelf.controller;
import com.example.GreenSelf.Dto.NurseryDto;
import com.example.GreenSelf.Dto.SellerDto;
import com.example.GreenSelf.Dto.SellerRequestDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.Nursery;
import com.example.GreenSelf.entity.Seller;
import com.example.GreenSelf.repo.SellerRepo;
import com.example.GreenSelf.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
public class AdminController {
    @Autowired
    AdminService adminService;
    @PostMapping("/admin/register")
    void createAdmin(@RequestBody UserRequestDto userRequestDto){
        adminService.createAdmin(userRequestDto);
    }
    @PatchMapping("/admin/verifySeller/")
     String verifyRequestSeller(@RequestBody SellerRequestDto sellerRequestDto){
          return  adminService.verifyRequestSeller(sellerRequestDto);
     }
    @PatchMapping("/admin/verifyNursery/{nurseryId}")
     String verifyNursery(@AuthenticationPrincipal UserDetails userDetails, @PathVariable int nurseryId){
            return adminService.verifyNusery(userDetails.getUsername(),nurseryId);
     }
     @GetMapping("admin/nursery")
    Page<NurseryDto> findAllNursery(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable){
        return adminService.findAllNursery(userDetails.getUsername(),pageable);
     }
     @GetMapping("admin/getSeller")
    Page<SellerDto> findSeller(@AuthenticationPrincipal UserDetails userDetails,Pageable pageable){
        return adminService.findAllseller(userDetails.getUsername(),pageable);
    }
}
