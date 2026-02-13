package com.example.GreenSelf.controller;
import com.example.GreenSelf.Dto.InventoryProductDto;
import com.example.GreenSelf.Dto.NurseryDto;
import com.example.GreenSelf.Dto.ProductDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.Nursery;
import com.example.GreenSelf.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class SellerController {
    @Autowired
    SellerService service;
    @PostMapping("/seller/register")
    void register(@RequestBody UserRequestDto userRequestDto){
          service.createSeller(userRequestDto);
    }
    @PostMapping("/seller/nursery")
    void  addNursery(@RequestBody NurseryDto nurseryDto, @AuthenticationPrincipal UserDetails userDetails){
          service.addNursery(nurseryDto,userDetails.getUsername());
    }
    @PostMapping("/seller/nursery/{nurseryId}")
    void addProduct(@AuthenticationPrincipal UserDetails userDetails, @PathVariable int nurseryId, @RequestBody ProductDto productDto){
          service.addProduct(userDetails.getUsername(), nurseryId,productDto);
    }
     @GetMapping("seller/nursery")
     Page<NurseryDto> getNursery(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable){
       return service.findNursery(userDetails.getUsername(),pageable);
     }
     @GetMapping("seller/nursery/{nurserId}/getInventoryProductList")
      Page<InventoryProductDto> getInventoryProduct(@AuthenticationPrincipal UserDetails userDetails,@PathVariable int nuseryId, Pageable pageable){
          return service.findInventoryProduct(userDetails.getUsername(),nuseryId,pageable);
     }


}
