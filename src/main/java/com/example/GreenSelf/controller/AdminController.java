package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.NurseryDto;
import com.example.GreenSelf.Dto.SellerDto;
import com.example.GreenSelf.Dto.SellerRequestDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.service.AdminService;
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

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "Administrative operations for managing users, nurseries, and system statistics")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private SellerService sellerService;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system statistics", description = "Retrieves system-wide statistics for admin dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Mock statistics for now - replace with actual service calls
        stats.put("totalUsers", 1250);
        stats.put("totalSellers", 85);
        stats.put("totalNurseries", 92);
        stats.put("totalProducts", 3420);
        stats.put("totalOrders", 5678);
        stats.put("pendingVerifications", 12);
        stats.put("revenue", 2845000.00);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create admin", description = "Creates a new admin user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "Admin already exists", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> createAdmin(
            @Valid @RequestBody @Parameter(description = "Admin registration details") UserRequestDto userRequestDto) {
        final User admin = adminService.createAdmin(userRequestDto);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", admin.getId());
        response.put("message", "Admin created successfully");
        response.put("username", admin.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/verify-seller")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify seller request", description = "Approves or rejects a seller verification request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller verification status updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Seller not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Map<String, String>> verifyRequestSeller(
            @Valid @RequestBody @Parameter(description = "Seller verification request") SellerRequestDto sellerRequestDto) {
        String result = adminService.verifyRequestSeller(sellerRequestDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/verify-nursery/{nurseryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verify nursery", description = "Approves a nursery for listing products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nursery verified successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Nursery not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Map<String, String>> verifyNursery(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Parameter(description = "ID of nursery to verify") int nurseryId) {

        String result = adminService.verifyNusery(userDetails.getUsername(), nurseryId);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nurseries")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all nurseries", description = "Retrieves a paginated list of all nurseries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved nurseries", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Page<NurseryDto>> findAllNursery(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<NurseryDto> nurseries = adminService.findAllNursery(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(nurseries);
    }

    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all sellers", description = "Retrieves a paginated list of all sellers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sellers", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Page<SellerDto>> findAllSellers(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<SellerDto> sellers = adminService.findAllseller(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/search-seller")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search seller by user username", description = "Retrieves a paginated list of sellers matching the user username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matched sellers", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Page<SellerDto>> searchSellerByUsername(
            @RequestParam @Parameter(description = "Username to search for") String username,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<SellerDto> sellers = sellerService.searchSellerByUsername(username, pageable);
        return ResponseEntity.ok(sellers);
    }

    @GetMapping("/search-nursery")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search nursery by name", description = "Retrieves a paginated list of nurseries matching the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matched nurseries", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    public ResponseEntity<Page<NurseryDto>> searchNurseryByName(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @Parameter(description = "Name of the nursery to search for") String name,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<NurseryDto> nurseries = adminService.searchNurseryByName(userDetails.getUsername(), name, pageable);
        return ResponseEntity.ok(nurseries);
    }
}