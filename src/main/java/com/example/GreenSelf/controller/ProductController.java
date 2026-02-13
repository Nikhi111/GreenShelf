package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.ProductResponseDto;
import com.example.GreenSelf.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

@RestController
public class ProductController{
    @Autowired
    ProductService productService;
    @GetMapping("/public/getproducts")
    Page<ProductResponseDto> getProdcuts(Pageable pageable){
        return productService.getProducts(pageable);
    }
    @GetMapping("/search/product")
    Page<ProductResponseDto> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size ){
        Pageable pageable = PageRequest.of(page, size);
        return productService.searchProducts(keyword,pageable);
    }

}
