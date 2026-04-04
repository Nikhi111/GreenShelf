package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.Dto.OrderResponse;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.Address;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.service.UserService;
import com.example.GreenSelf.util.JwtUtil;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "User Management", description = "User authentication, registration, and operations")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Missing username or password",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody @Parameter(description = "Login credentials") User request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Logged in successfully");
        response.put("username", request.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/register")
    @Operation(summary = "Register new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid registration data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Username already exists",
                    content = @Content)
    })
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody @Parameter(description = "User registration details") UserRequestDto userRequestDto) {

        final User user = userService.createUser(userRequestDto);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("message", "User created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/public/home")
    @Operation(summary = "Home page", description = "Public homepage endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to GreenSelf - Your Plant Marketplace");
    }

    @GetMapping("/user/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user", description = "Returns authenticated user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, String> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("message", "Hello, " + userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/cart")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add to cart", description = "Adds a product to user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to cart successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid cart data",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody @Parameter(description = "Cart item details") CartDto cartDto) {

        userService.addToCart(userDetails.getUsername(), cartDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item added to cart successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/cart")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get cart", description = "Retrieves user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content)
    })
    public ResponseEntity<?> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        Object cart = userService.getCart(userDetails.getUsername());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/user/cart/{itemId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove from cart", description = "Removes an item from user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Cart item not found",
                    content = @Content)
    })
    public ResponseEntity<Map<String, String>> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Parameter(description = "Product ID in Cart") Integer itemId) {

        userService.removeFromCart(userDetails.getUsername(), itemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user/cart/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update cart quantity", description = "Updates quantity of a product in the cart (max 10)")
    public ResponseEntity<Map<String, String>> updateCartQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer productId,
            @RequestParam Integer count) {

        userService.updateCartItemQuantity(userDetails.getUsername(), productId, count);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Quantity updated successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/order/create")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create order", description = "Creates a new order from user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data or empty cart",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content)
    })
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody @Parameter(description = "Delivery address") Address address) {

        OrderResponse response = userService.createOrder(userDetails.getUsername(), address);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/orders")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get user orders", description = "Retrieves all orders for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content)
    })
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<OrderResponse> orders = userService.getUserOrders(userDetails.getUsername());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/orders/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get order details", description = "Retrieves details of a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Not your order",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    public ResponseEntity<OrderResponse> getOrderDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Parameter(description = "Order ID") Long orderId) {

        OrderResponse orderDetails = userService.getOrderDetails(userDetails.getUsername(), orderId);
        return ResponseEntity.ok(orderDetails);
    }
}