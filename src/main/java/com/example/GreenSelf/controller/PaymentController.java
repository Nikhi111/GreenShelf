package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.PaymentVerifyRequest;
import com.example.GreenSelf.service.PaymentService;
import com.example.GreenSelf.service.PayoutService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment Management", description = "Payment and payout operations using Razorpay")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PayoutService payoutService;

    @PostMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verifies Razorpay payment signature after customer completes checkout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment verified successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Payment verification failed - invalid signature",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid payment data",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> verifyPayment(
            @Valid @RequestBody @Parameter(description = "Payment verification details") PaymentVerifyRequest request) {

        boolean verified = paymentService.verifyPayment(request);
        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("status", "success");
            response.put("message", "Payment verified successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failed");
            response.put("message", "Payment verification failed - invalid signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/payout/{orderId}")
    @Operation(summary = "Process payout", description = "Triggers payout to nursery/seller after delivery confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payout processed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Payout failed",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> processPayout(
            @PathVariable @Parameter(description = "ID of the order for payout") Long orderId) {

        Map<String, String> response = new HashMap<>();

        try {
            String payoutId = payoutService.processPayout(orderId);
            response.put("status", "success");
            response.put("message", "Payout processed successfully");
            response.put("payoutId", payoutId);
            response.put("orderId", orderId.toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "failed");
            response.put("message", "Payout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/status/{orderId}")
    @Operation(summary = "Get payment status", description = "Retrieves the payment status for a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment status retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> getPaymentStatus(
            @PathVariable @Parameter(description = "Order ID") Long orderId) {

        // Placeholder for payment status check
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId.toString());
        response.put("message", "Implement payment status check logic");

        return ResponseEntity.ok(response);
    }
}