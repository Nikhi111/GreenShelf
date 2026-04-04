package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.ProductDto;
import com.example.GreenSelf.Dto.ProductResponseDto;
import com.example.GreenSelf.entity.Product;
import com.example.GreenSelf.entity.Plant;
import com.example.GreenSelf.Dto.ProductDetailedResponseDto;
import com.example.GreenSelf.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    @Autowired
    ProductRepo productRepo;
    Page<ProductResponseDto> productToPoductResponseDto(Page<Product> products){
        return   products.map(product -> {
            ProductResponseDto productResponseDto=new ProductResponseDto();
            productResponseDto.setId(product.getId());
            productResponseDto.setProductImage(product.getProductImage());
            productResponseDto.setDsc(product.getDescription());
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

    public ProductResponseDto getProductById(int id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setProductImage(product.getProductImage());
        dto.setDsc(product.getDescription());
        dto.setName(product.getName());
        dto.setPrize(product.getPrize());
        if (product.getNursery() != null) {
            dto.setNurseryName(product.getNursery().getName());
        }
        return dto;
    }

    public Page<ProductResponseDto> getProductsByCategory(String category, Pageable pageable) {
        Page<Product> products = productRepo.findByCategoryIgnoreCase(category, pageable);
        return productToPoductResponseDto(products);
    }

    public Page<ProductResponseDto> getProductsByNursery(int nurseryId, Pageable pageable) {
        Page<Product> products = productRepo.findByNurseryId(nurseryId, pageable);
        return productToPoductResponseDto(products);
    }
    
    public ProductDetailedResponseDto getProductDetailsById(int id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ProductDetailedResponseDto dto = new ProductDetailedResponseDto();
        dto.setId(product.getId());
        dto.setProductImage(product.getProductImage());
        dto.setDsc(product.getDescription());
        dto.setName(product.getName());
        dto.setPrize(product.getPrize());
        if (product.getNursery() != null) {
            dto.setNurseryName(product.getNursery().getName());
        }
        
        Plant plant = product.getPlant();
        if (plant != null) {
            dto.setScientificName(plant.getScientificName());
            dto.setWateringFrequency(plant.getWateringFrequency());
            dto.setWateringDays(plant.getWateringDays());
            dto.setSunlight(plant.getSunlight());
            dto.setCareLevel(plant.getCareLevel());
            dto.setToxicToPets(plant.isToxicToPets());
            dto.setToxicToHumans(plant.isToxicToHumans());
            dto.setIndoorPlant(plant.isIndoorPlant());
            dto.setHardinessMin(plant.getHardinessMin());
            dto.setHardinessMax(plant.getHardinessMax());
            dto.setGrowthRate(plant.getGrowthRate());
            dto.setSoilType(plant.getSoilType());
            dto.setFloweringSeason(plant.getFloweringSeason());
        }
        return dto;
    }
}
