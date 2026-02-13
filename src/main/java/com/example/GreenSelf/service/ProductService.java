package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.ProductDto;
import com.example.GreenSelf.Dto.ProductResponseDto;
import com.example.GreenSelf.entity.Product;
import com.example.GreenSelf.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;


@Service
public class ProductService {
    @Autowired
    ProductRepo productRepo;
    Page<ProductResponseDto> productToPoductResponseDto(Page<Product> products){
        return   products.map(product -> {
            ProductResponseDto productResponseDto=new ProductResponseDto();
            productResponseDto.setId(product.getId());
            productResponseDto.setProductImage(product.getProductImage());
            productResponseDto.setDsc(productResponseDto.getDsc());
            productResponseDto.setName(product.getName());
            productResponseDto.setPrize(product.getPrize());
            if (product.getNursery()!=null){
                productResponseDto.setNurseryName(product.getNursery().getName());
            }
            return productResponseDto;
        });
    }
    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Product> products=productRepo.findAll(pageable);
       return productToPoductResponseDto(products);
    }
    public Page<ProductResponseDto> searchProducts(String keyword, Pageable pageable) {
        Page<Product> products=productRepo.findByNameContainingIgnoreCase(keyword,pageable);
        return productToPoductResponseDto(products);
    }
}
