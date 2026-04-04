package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.Dto.OrderResponse;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.*;
import com.example.GreenSelf.repo.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired OrderRepo orderRepo;
    @Autowired AddressRepo addressRepo;
    @Autowired InventoryProductRepo inventoryProductRepo;
    @Autowired UserRepo userRepo;
    @Autowired CartProductRepo cartProductRepo;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ProductRepo productRepo;
    @Autowired CartRepo cartRepo;
    @Autowired PaymentRepo paymentRepo;
    @Autowired RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;
    @Value("${platform.commission.percent}")
    private double commissionPercent;

    // ── Create User ───────────────────────────────────────────────────────────
    @Transactional
    public User createUser(UserRequestDto userRequestDto) {
        Optional<User> data = userRepo.findUserByUsername(userRequestDto.getUsername());
        if (data.isPresent())
            throw new RuntimeException("User already exists: " + userRequestDto.getUsername());

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.USER);
        return userRepo.save(user);
    }

    // ── Add to Cart ───────────────────────────────────────────────────────────
    @Transactional
    public void addToCart(String username, CartDto cartDto) {

        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(cartDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!checkProductQuantity(product, cartDto.getCount()))
            throw new RuntimeException("Out of stock");

        Optional<Cart> cartData = cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE);

        Cart cart;
        if (cartData.isPresent()) {
            cart = cartData.get();
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartStatus(CartStatus.ACTIVE);
            cart.setTotalPrize(0);
        }

        // Check if product already exists in cart
        Optional<CartProduct> existingProductOpt = cart.getCartproductList() != null ?
                cart.getCartproductList().stream()
                        .filter(cp -> cp.getProduct().getId() == product.getId())
                        .findFirst() : Optional.empty();

        if(existingProductOpt.isPresent()){
            CartProduct cp = existingProductOpt.get();
            int newCount = cp.getCount() + cartDto.getCount();
            if (newCount > 10) throw new RuntimeException("Maximum 10 items allowed per product");
            cp.setCount(newCount);
            cp.setPrizetotal(product.getPrize() * newCount);
            cartProductRepo.save(cp);
        } else {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setProduct(product);
            cartProduct.setCount(cartDto.getCount());
            cartProduct.setPrizetotal(product.getPrize() * cartDto.getCount());
            
            CartProduct savedCartProduct = cartProductRepo.save(cartProduct);
            cart.addCartProduct(savedCartProduct);
        }
        
        recalculateCartTotal(cart);
        cartRepo.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        if(cart.getCartproductList() == null) return;
        int total = cart.getCartproductList().stream().mapToInt(CartProduct::getPrizetotal).sum();
        cart.setTotalPrize(total);
    }

    @Transactional
    public void updateCartItemQuantity(String username, int productId, int count) {
        if (count < 1 || count > 10) throw new RuntimeException("Quantity must be between 1 and 10");

        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active cart"));

        CartProduct existingProduct = cart.getCartproductList().stream()
                .filter(cp -> cp.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        int currentCount = existingProduct.getCount();
        if(count > currentCount) {
             // We need more inventory
             if(!checkProductQuantity(existingProduct.getProduct(), count - currentCount)){
                 throw new RuntimeException("Out of stock for additional quantity");
             }
        } else if (count < currentCount) {
             // We are returning inventory (safely)
             inventoryProductRepo.findByProduct(existingProduct.getProduct()).ifPresent(inv -> {
                 inv.setCount(inv.getCount() + (currentCount - count));
                 inventoryProductRepo.save(inv);
             });
        }

        existingProduct.setCount(count);
        existingProduct.setPrizetotal(existingProduct.getProduct().getPrize() * count);
        cartProductRepo.save(existingProduct);
        recalculateCartTotal(cart);
        cartRepo.save(cart);
    }

    @Transactional
    public void removeFromCart(String username, int productId) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active cart"));

        CartProduct existingProduct = cart.getCartproductList().stream()
                .filter(cp -> cp.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        // Return to inventory (safely — inventory entry may not exist for dummy data)
        inventoryProductRepo.findByProduct(existingProduct.getProduct()).ifPresent(inv -> {
            inv.setCount(inv.getCount() + existingProduct.getCount());
            inventoryProductRepo.save(inv);
        });

        // orphanRemoval=true on Cart.cartproductList handles the actual DB delete
        cart.getCartproductList().remove(existingProduct);
        recalculateCartTotal(cart);
        cartRepo.save(cart);
    }

    public Object getCart(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE).orElseGet(() -> {
            Cart emptyCart = new Cart();
            emptyCart.setUser(user);
            emptyCart.setTotalPrize(0);
            return emptyCart;
        });
    }

    // ── Check Product Quantity ─────────────────────────────────────────────────
    boolean checkProductQuantity(Product product, int count) {
        if (count < 0) throw new RuntimeException("Invalid quantity");

        InventoryProduct inventoryProduct = inventoryProductRepo.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory product not found"));

        if (inventoryProduct.getCount() < count) return false;

        inventoryProduct.setCount(inventoryProduct.getCount() - count);
        return true;
    }

    // ── Create Order + Razorpay Order ─────────────────────────────────────────
    @Transactional
    public OrderResponse createOrder(String username, Address address) {

        // 1. Get user
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get active cart
        Cart cart = cartRepo.findByUserAndCartStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No active cart found"));

        List<CartProduct> cartProducts = cart.getCartproductList();

        if (cartProducts == null || cartProducts.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Expire cart
        cart.setCartStatus(CartStatus.EXPIRED);
        cartRepo.save(cart);

        // 4. Save address
        address.setUser(user);
        Address savedAddress = addressRepo.save(address);

        // 5. Calculate amounts
        double totalPrice      = cart.getTotalPrize();
        double commission      = (totalPrice * commissionPercent) / 100;
        double sellerAmount    = totalPrice - commission;

        // 6. Create Razorpay order
        String razorpayOrderId = createRazorpayOrder(totalPrice);

        // 7. Build Order entity
        Order order = new Order();
        order.setUser(user);
        order.setAddress(savedAddress);
        order.setNursery(cartProducts.get(0).getProduct().getNursery());
        order.setTotalPrice(totalPrice);
        order.setPlatformCommission(commission);
        order.setSellerAmount(sellerAmount);
        order.setRazorpayOrderId(razorpayOrderId);
        order.setOrderStatus(OrderStatus.CREATED);

        // 8. Build OrderProducts ✅ FIXED — was missing setOrder + save
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (CartProduct cartProduct : cartProducts) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);                          // ✅ FIXED
            orderProduct.setProduct(cartProduct.getProduct());
            orderProduct.setCount(cartProduct.getCount());
            orderProduct.setPrize(cartProduct.getPrizetotal());
            orderProducts.add(orderProduct);
        }

        order.setOrderProducts(orderProducts);
        Order savedOrder = orderRepo.save(order);                  // cascades to orderProducts

        // 9. Save Payment record as PENDING
        Payment payment = new Payment();
        payment.setOrderId((long) savedOrder.getId());
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmount(totalPrice);
        payment.setCurrency("INR");
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepo.save(payment);

        // 10. Build and return response
        return buildOrderResponse(savedOrder, user, savedAddress, cartProducts, razorpayOrderId, totalPrice, commission, sellerAmount);
    }

    // ── Call Razorpay Orders API ──────────────────────────────────────────────
    private String createRazorpayOrder(double totalPrice) {
        System.out.println("KEY ID: [" + keyId + "]");
        System.out.println("KEY SECRET: [" + keySecret + "]");
        try {
            JSONObject options = new JSONObject();
            options.put("amount",   (int)(totalPrice * 100)); // convert to paise
            options.put("currency", "INR");
            options.put("receipt",  "order_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);
            return razorpayOrder.get("id");

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    // ── Build OrderResponse from saved data ───────────────────────────────────
    private OrderResponse buildOrderResponse(
            Order order, User user, Address address,
            List<CartProduct> cartProducts, String razorpayOrderId,
            double totalPrice, double commission, double sellerAmount) {

        OrderResponse response = new OrderResponse();

        // Razorpay fields
        response.setRazorpayOrderId(razorpayOrderId);
        response.setKeyId(keyId);
        response.setAmount(totalPrice);
        response.setCurrency("INR");

        // Order fields
        response.setOrderId((long) order.getId());
        response.setOrderStatus(order.getOrderStatus());
        response.setTotalPrice(totalPrice);
        response.setPlatformCommission(commission);
        response.setSellerAmount(sellerAmount);
        response.setCreatedAt(order.getCreatedAt());

        // User
        response.setUsername(user.getUsername());

        // Nursery
        if (order.getNursery() != null) {
            response.setNurseryName(order.getNursery().getName());
        }

        // Address
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setAddressLine3(address.getAddressLine3());
        response.setPincode(address.getPincode());

        // Products
        List<OrderResponse.OrderProductResponse> productResponses = new ArrayList<>();
        for (CartProduct cp : cartProducts) {
            OrderResponse.OrderProductResponse pr = new OrderResponse.OrderProductResponse();
            pr.setProductId(cp.getProduct().getId());
            pr.setProductName(cp.getProduct().getName());
            pr.setCount(cp.getCount());
            pr.setPrize(cp.getPrizetotal());
            productResponses.add(pr);
        }
        response.setProducts(productResponses);

        return response;
    }

    public List<OrderResponse> getUserOrders(String username) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Order> orders = orderRepo.findByUserOrderByCreatedAtDesc(user);
        
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(buildOrderResponseFromOrder(order));
        }
        return responses;
    }

    public OrderResponse getOrderDetails(String username, Long orderId) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Order order = orderRepo.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or not belonging to you"));
        
        return buildOrderResponseFromOrder(order);
    }

    private OrderResponse buildOrderResponseFromOrder(Order order) {
        OrderResponse response = new OrderResponse();

        response.setRazorpayOrderId(order.getRazorpayOrderId());
        response.setKeyId(keyId);
        response.setAmount(order.getTotalPrice());
        response.setCurrency("INR");

        response.setOrderId((long) order.getId());
        response.setOrderStatus(order.getOrderStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setPlatformCommission(order.getPlatformCommission());
        response.setSellerAmount(order.getSellerAmount());
        response.setCreatedAt(order.getCreatedAt());

        response.setUsername(order.getUser().getUsername());

        if (order.getNursery() != null) {
            response.setNurseryName(order.getNursery().getName());
        }

        if (order.getAddress() != null) {
            response.setAddressLine1(order.getAddress().getAddressLine1());
            response.setAddressLine2(order.getAddress().getAddressLine2());
            response.setAddressLine3(order.getAddress().getAddressLine3());
            response.setPincode(order.getAddress().getPincode());
        }

        List<OrderResponse.OrderProductResponse> productResponses = new ArrayList<>();
        if (order.getOrderProducts() != null) {
            for (OrderProduct op : order.getOrderProducts()) {
                OrderResponse.OrderProductResponse pr = new OrderResponse.OrderProductResponse();
                pr.setProductId(op.getProduct().getId());
                pr.setProductName(op.getProduct().getName());
                pr.setCount(op.getCount());
                pr.setPrize(op.getPrize());
                productResponses.add(pr);
            }
        }
        response.setProducts(productResponses);

        return response;
    }
}