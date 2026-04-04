package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.*;
import com.example.GreenSelf.entity.Nursery;
import com.example.GreenSelf.entity.OrderStatus;
import com.example.GreenSelf.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
@Tag(name = "Seller Management", description = "Seller operations for managing nurseries and products")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class SellerController {

        @Autowired
        private SellerService service;

        @PostMapping("/register")
        @Operation(summary = "Register as seller", description = "Creates a new seller account")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Seller registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SellerResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Seller already exists", content = @Content)
        })
        public ResponseEntity<SellerResponseDto> register(
                        @Valid @RequestBody @Parameter(description = "Seller registration details") UserRequestDto userRequestDto) {

                final SellerResponseDto seller = service.createSeller(userRequestDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(seller);
        }

        @PostMapping("/nursery")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Add a nursery", description = "Creates a new nursery for the seller")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Nursery created successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Invalid nursery data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - seller not verified", content = @Content)
        })
        public ResponseEntity<Map<String, Object>> addNursery(
                        @Valid @RequestBody @Parameter(description = "Nursery details") NurseryDto nurseryDto,
                        @AuthenticationPrincipal UserDetails userDetails) {

                final Nursery nursery = service.addNursery(nurseryDto, userDetails.getUsername());
                Map<String, Object> response = new HashMap<>();
                response.put("nurseryId", nursery.getId());
                response.put("message", "Nursery created successfully");
                response.put("name", nursery.getName());
                response.put("status", "pending_verification");

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/nursery/{nurseryId}/product")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Add product to nursery", description = "Adds a new product to seller's nursery inventory")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product added successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Invalid product data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied - not your nursery or nursery not verified", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Nursery not found", content = @Content)
        })
        public ResponseEntity<Map<String, String>> addProduct(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable @Parameter(description = "Nursery ID") int nurseryId,
                        @Valid @RequestBody @Parameter(description = "Product details") ProductDto productDto) {

                service.addProduct(userDetails.getUsername(), nurseryId, productDto);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Product added successfully");
                response.put("nurseryId", String.valueOf(nurseryId));

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/nursery")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Get seller's nurseries", description = "Retrieves all nurseries belonging to the seller")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Nurseries retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
        })
        public ResponseEntity<Page<NurseryDto>> getNurseries(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @Parameter(description = "Pagination parameters") Pageable pageable) {

                Page<NurseryDto> nurseries = service.findNursery(userDetails.getUsername(), pageable);
                return ResponseEntity.ok(nurseries);
        }

        @GetMapping("/nursery/{nurseryId}/inventory")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Get nursery inventory", description = "Retrieves all products in seller's nursery")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied - not your nursery", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Nursery not found", content = @Content)
        })
        public ResponseEntity<Page<InventoryProductDto>> getInventoryProducts(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable @Parameter(description = "Nursery ID") int nurseryId,
                        @Parameter(description = "Pagination parameters") Pageable pageable) {

                Page<InventoryProductDto> inventory = service.findInventoryProduct(
                                userDetails.getUsername(), nurseryId, pageable);
                return ResponseEntity.ok(inventory);
        }

        @PatchMapping("/nursery/{nurseryId}/product/{productId}")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Update product", description = "Updates product details in seller's nursery")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product or nursery not found", content = @Content)
        })
        public ResponseEntity<Map<String, String>> updateProduct(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable @Parameter(description = "Nursery ID") int nurseryId,
                        @PathVariable @Parameter(description = "Product ID") Long productId,
                        @Valid @RequestBody @Parameter(description = "Updated product details") ProductDto productDto) {

                service.updateProduct(userDetails.getUsername(), nurseryId, productId.intValue(), productDto);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Product updated successfully");
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/nursery/{nurseryId}/product/{productId}")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Delete product", description = "Removes a product from seller's nursery")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
        })
        public ResponseEntity<Map<String, String>> deleteProduct(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable @Parameter(description = "Nursery ID") int nurseryId,
                        @PathVariable @Parameter(description = "Product ID") Long productId) {

                service.deleteProduct(userDetails.getUsername(), nurseryId, productId.intValue());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Product deleted successfully");
                return ResponseEntity.ok(response);
        }

        @GetMapping("/nursery/{nurseryId}/dashboard/product-performance")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Get product performance", description = "Retrieves sales and inventory metrics per product for dashboard")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Performance metrics retrieved successfully",
                        content = @Content(mediaType = "application/json")),
                @ApiResponse(responseCode = "403", description = "Access denied",
                        content = @Content),
                @ApiResponse(responseCode = "404", description = "Nursery not found",
                        content = @Content)
        })
        public ResponseEntity<List<ProductPerformanceDto>> getProductPerformance(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable @Parameter(description = "Nursery ID") int nurseryId) {

            List<ProductPerformanceDto> performance = service.getProductPerformance(userDetails.getUsername(), nurseryId);
            return ResponseEntity.ok(performance);
        }

        @GetMapping("/nursery/{nurseryId}/orders")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Get nursery orders", description = "Retrieves all orders placed for a specific nursery")
        public ResponseEntity<List<OrderResponse>> getNurseryOrders(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable @Parameter(description = "Nursery ID") int nurseryId) {

            List<OrderResponse> orders = service.getNurseryOrders(userDetails.getUsername(), nurseryId);
            return ResponseEntity.ok(orders);
        }

        @PatchMapping("/nursery/{nurseryId}/orders/{orderId}/status")
        @PreAuthorize("hasRole('SELLER')")
        @Operation(summary = "Update order status", description = "Updates order status (e.g. to CONFIRMED, DELIVERED)")
        public ResponseEntity<OrderResponse> updateOrderStatus(
                @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable @Parameter(description = "Nursery ID") int nurseryId,
                @PathVariable @Parameter(description = "Order ID") Long orderId,
                @RequestParam @Parameter(description = "New Status") OrderStatus status) {

            OrderResponse updatedOrder = service.updateOrderStatus(userDetails.getUsername(), nurseryId, orderId, status);
            return ResponseEntity.ok(updatedOrder);
        }
}