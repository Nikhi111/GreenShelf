package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.ProductResponseDto;
import com.example.GreenSelf.Dto.ProductDetailedResponseDto;
import com.example.GreenSelf.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController MockMvc Tests")
class ProductControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ProductService productService;
    @MockBean UserDetailsService userDetailsService;
    @MockBean com.example.GreenSelf.util.JwtUtil jwtUtil;

    // ── GET /api/products/public ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/public: returns 200 with paginated products")
    void getProducts_returnsPagedResult() throws Exception {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(1);
        dto.setName("Snake Plant");
        dto.setPrize(350);

        when(productService.getProducts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/products/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Snake Plant"))
                .andExpect(jsonPath("$.content[0].prize").value(350))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/products/public: returns 200 with empty page when no products")
    void getProducts_emptyList_returns200() throws Exception {
        when(productService.getProducts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/products/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ── GET /api/products/search ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/search?keyword=plant: returns 200 with matching products")
    void searchProducts_validKeyword_returns200() throws Exception {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(2);
        dto.setName("Peace Plant");
        dto.setPrize(200);

        when(productService.searchProducts(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/products/search").param("keyword", "plant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Peace Plant"));
    }

    @Test
    @DisplayName("GET /api/products/search: missing keyword returns 400")
    void searchProducts_missingKeyword_returns400() throws Exception {
        mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/products/public with pagination ──────────────────────────────

    @Test
    @DisplayName("GET /api/products/public?size=5&page=0: respects pagination params")
    void getProducts_withPagination_returns200() throws Exception {
        when(productService.getProducts(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/products/public")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    // ── GET /api/products/details/{productId} ─────────────────────────────────

    @Test
    @DisplayName("GET /api/products/details/{productId}: returns 200 with detailed properties")
    void getProductDetails_returns200() throws Exception {
        ProductDetailedResponseDto dto = new ProductDetailedResponseDto();
        dto.setId(1);
        dto.setName("Snake Plant");
        dto.setPrize(350);
        dto.setWateringDays("Every 2 weeks");
        dto.setSunlight("Indirect sunlight");

        when(productService.getProductDetailsById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/products/details/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Snake Plant"))
                .andExpect(jsonPath("$.prize").value(350))
                .andExpect(jsonPath("$.wateringDays").value("Every 2 weeks"))
                .andExpect(jsonPath("$.sunlight").value("Indirect sunlight"));
    }
}
