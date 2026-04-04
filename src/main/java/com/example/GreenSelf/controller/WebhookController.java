package com.example.GreenSelf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@Tag(name = "Webhooks", description = "Webhook endpoints for external service integrations")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/razorpay")
    @Operation(
            summary = "Razorpay webhook handler",
            description = "Handles Razorpay payment webhooks for events like payment.captured, payment.failed, etc.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Razorpay webhook payload",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"event\": \"payment.captured\", \"payload\": {...}}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook processed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid signature",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Webhook processing error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, String>> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") @Parameter(description = "Razorpay signature for verification") String signature) {

        Map<String, String> response = new HashMap<>();

        try {
            // 1. Verify signature
            String generatedSignature = generateHmacSha256(payload, webhookSecret);

            if (!generatedSignature.equals(signature)) {
                logger.warn("Invalid Razorpay webhook signature received");
                response.put("status", "error");
                response.put("message", "Invalid signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 2. Handle different webhook events
            if (payload.contains("payment.captured")) {
                logger.info("Payment captured webhook received");
                handlePaymentCaptured(payload);
                response.put("event", "payment.captured");
            } else if (payload.contains("payment.failed")) {
                logger.warn("Payment failed webhook received");
                handlePaymentFailed(payload);
                response.put("event", "payment.failed");
            } else if (payload.contains("payment.authorized")) {
                logger.info("Payment authorized webhook received");
                handlePaymentAuthorized(payload);
                response.put("event", "payment.authorized");
            } else if (payload.contains("order.paid")) {
                logger.info("Order paid webhook received");
                handleOrderPaid(payload);
                response.put("event", "order.paid");
            } else if (payload.contains("refund.created")) {
                logger.info("Refund created webhook received");
                handleRefundCreated(payload);
                response.put("event", "refund.created");
            } else {
                logger.info("Unknown webhook event received: {}", payload.substring(0, Math.min(100, payload.length())));
                response.put("event", "unknown");
            }

            response.put("status", "success");
            response.put("message", "Webhook processed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing Razorpay webhook: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Webhook processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Webhook health check", description = "Health check endpoint for webhook service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook service is healthy",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "webhook");
        return ResponseEntity.ok(response);
    }

    // Private helper methods for webhook event handling

    private void handlePaymentCaptured(String payload) {
        // TODO: Implement payment captured logic
        // - Update order status to "paid"
        // - Send confirmation email
        // - Trigger fulfillment process
        logger.info("Processing payment.captured event");
    }

    private void handlePaymentFailed(String payload) {
        // TODO: Implement payment failed logic
        // - Update order status to "payment_failed"
        // - Send notification to user
        // - Log failed payment details
        logger.warn("Processing payment.failed event");
    }

    private void handlePaymentAuthorized(String payload) {
        // TODO: Implement payment authorized logic
        // - Update order status to "authorized"
        logger.info("Processing payment.authorized event");
    }

    private void handleOrderPaid(String payload) {
        // TODO: Implement order paid logic
        // - Confirm order completion
        // - Initiate delivery process
        logger.info("Processing order.paid event");
    }

    private void handleRefundCreated(String payload) {
        // TODO: Implement refund created logic
        // - Update order status to "refunded"
        // - Send refund confirmation
        logger.info("Processing refund.created event");
    }

    /**
     * Generates HMAC SHA256 signature for webhook verification
     *
     * @param data   Webhook payload
     * @param secret Webhook secret key
     * @return Hex-encoded signature
     * @throws Exception If signature generation fails
     */
    private String generateHmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}