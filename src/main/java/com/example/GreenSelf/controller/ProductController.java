package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.ProductResponseDto;
import com.example.GreenSelf.Dto.ProductDetailedResponseDto;
import com.example.GreenSelf.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Catalog", description = "Public product browsing and search operations")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/public")
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all available products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content)
    })
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @Parameter(description = "Pagination and sorting parameters") Pageable pageable) {

        Page<ProductResponseDto> products = productService.getProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches products by keyword in name, description, or category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content)
    })
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(
            @RequestParam @NotBlank @Parameter(description = "Search keyword", required = true) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) @Parameter(description = "Page number (0-indexed)") int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) @Parameter(description = "Page size (1-100)") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> products = productService.searchProducts(keyword, pageable);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details", description = "Retrieves detailed information about a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable @Parameter(description = "Product ID") Long productId) {

        ProductResponseDto product = productService.getProductById(productId.intValue());
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/details/{productId}")
    @Operation(summary = "Get exact product details along with botanical plant properties", description = "Retrieves deeply detailed information about a specific product and its plant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailedResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<ProductDetailedResponseDto> getProductDetails(
            @PathVariable @Parameter(description = "Product ID") Long productId) {

        ProductDetailedResponseDto product = productService.getProductDetailsById(productId.intValue());
        return ResponseEntity.ok(product);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves products filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content)
    })
    public ResponseEntity<Page<ProductResponseDto>> getProductsByCategory(
            @PathVariable @Parameter(description = "Product category") String category,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<ProductResponseDto> products = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/nursery/{nurseryId}")
    @Operation(summary = "Get products by nursery", description = "Retrieves all products from a specific nursery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Nursery not found",
                    content = @Content)
    })
    public ResponseEntity<Page<ProductResponseDto>> getProductsByNursery(
            @PathVariable @Parameter(description = "Nursery ID") Long nurseryId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<ProductResponseDto> products = productService.getProductsByNursery(nurseryId.intValue(), pageable);
        return ResponseEntity.ok(products);
    }
}