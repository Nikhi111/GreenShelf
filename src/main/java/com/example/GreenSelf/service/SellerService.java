package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.*;
import com.example.GreenSelf.entity.*;
import com.example.GreenSelf.repo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Service
public class SellerService {
    private final AddressRepo addressRepo;
    private final InventoryRepo inventoryRepo;
    private final ProductRepo productRepo;
    private final InventoryProductRepo inventoryProductRepo;
    private final PlantRepo plantRepo;
    private final UserRepo userRepo;
    private final SellerRepo sellerRepo;
    private final NurseryRepo nurseryRepo;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepo orderRepo;

    public SellerService(
            AddressRepo addressRepo, InventoryRepo inventoryRepo,
            ProductRepo productRepo,
            InventoryProductRepo inventoryProductRepo,
            PlantRepo plantRepo,
            UserRepo userRepo,
            SellerRepo sellerRepo,
            NurseryRepo nurseryRepo,
            PasswordEncoder passwordEncoder,
            OrderRepo orderRepo) {
        this.addressRepo = addressRepo;
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.inventoryProductRepo = inventoryProductRepo;
        this.plantRepo = plantRepo;
        this.userRepo = userRepo;
        this.sellerRepo = sellerRepo;
        this.nurseryRepo = nurseryRepo;
        this.passwordEncoder = passwordEncoder;
        this.orderRepo = orderRepo;
    }

    @Transactional
    public SellerResponseDto createSeller(UserRequestDto userRequestDto) {
        if (userRepo.findUserByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(Role.VENDER);
        User storedUser = userRepo.save(user);

        Seller seller = new Seller();
        seller.setUser(storedUser);
        seller.setApprovedSeller(false);
        final Seller save = sellerRepo.save(seller);
        return new SellerResponseDto(save);
    }

    @Transactional
    public Nursery addNursery(NurseryDto nurseryDto, String username) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Address address = nurseryDto.getAddress();
        final Address savedAddress = addressRepo.save(address);
        Inventory inventory = new Inventory();
        final Inventory savedInventory = inventoryRepo.save(inventory);
        Nursery nursery = new Nursery();
        nursery.setName(nurseryDto.getName());
        nursery.setAddress(savedAddress);
        nursery.setInventory(savedInventory);
        nursery.setLicenceNumber(nurseryDto.getLicenceNumber());
        nursery.setSeller(seller);
        final Nursery savedNursery = nurseryRepo.save(nursery);
        savedInventory.setNursery(nursery);
        inventoryRepo.save(savedInventory);
        return savedNursery;
    }

    @Transactional
    public void addProduct(String username, int nurseryId, ProductDto productDto) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Nursery nursery = nurseryRepo.findByIdAndOwnerAndIsVerified(nurseryId, seller, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery not found"));

        Inventory inventory = nursery.getInventory();
        InventoryProduct inventoryProduct = new InventoryProduct();
        inventoryProduct.setInventory(inventory);
        inventoryProduct.setCount(productDto.getStock());
        Plant plant = plantRepo.findByPlantId(productDto.getPlantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not available"));
        Product product = new Product();
        product.setPlant(plant);
        product.setName(plant.getCommonName());
        product.setDescription(plant.getDescription());
        product.setProductImage(plant.getImageUrl());
        product.setNursery(nursery);
        product.setPrize(productDto.getPrize());
        Product savedProduct = productRepo.save(product);
        inventoryProduct.setProduct(savedProduct);
        inventoryProductRepo.save(inventoryProduct);
    }

    Page<NurseryDto> nurseryToNuseryDto(Page<Nursery> nurseries) {
        return nurseries.map(
                nursery -> {
                    NurseryDto nurseryDto = new NurseryDto();
                    nurseryDto.setAddress(nursery.getAddress());
                    nurseryDto.setName(nursery.getName());
                    nurseryDto.setLicenceNumber(nursery.getLicenceNumber());
                    return nurseryDto;
                });
    }

    public Page<NurseryDto> findNursery(String username, Pageable pageable) {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User does not exsist"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new RuntimeException("seller is not approved or not found"));
        Page<Nursery> nurseries = nurseryRepo.findByOwner(seller, pageable);
        return nurseryToNuseryDto(nurseries);
    }

    Page<InventoryProductDto> invProToinvProDto(Page<InventoryProduct> inventoryProducts) {
        return inventoryProducts.map(inventoryProduct -> {
            InventoryProductDto inventoryProductDto = new InventoryProductDto();
            inventoryProductDto.setProductId(inventoryProduct.getId());
            inventoryProductDto.setPrize(inventoryProduct.getProduct().getPrize());
            inventoryProductDto.setPlantName(inventoryProduct.getProduct().getName());
            inventoryProductDto.setQuantity(inventoryProduct.getCount());
            return inventoryProductDto;
        });
    }

    public Page<InventoryProductDto> findInventoryProduct(String username, int nuseryId, Pageable pageable) {
        Nursery nursery = nurseryRepo.findById(nuseryId)
                .orElseThrow(() -> new RuntimeException("Nursery does not exist"));
        if (!nursery.getOwner().getUser().getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden request");
        Inventory inventory = nursery.getInventory();
        List<InventoryProduct> inventoryProductsList = inventory.getProductList();
        Page<InventoryProduct> inventoryProducts = new PageImpl<>(inventoryProductsList, pageable,
                inventoryProductsList.size());
        return invProToinvProDto(inventoryProducts);
    }
    
    public Page<SellerDto> searchSellerByUsername(String username, Pageable pageable) {
        Page<Seller> sellers = sellerRepo.findByUserUsernameContainingIgnoreCase(username, pageable);
        return sellers.map(seller -> {
            SellerDto sellerDto = new SellerDto();
            sellerDto.setId(seller.getId());
            sellerDto.setName(seller.getUser().getUsername());
            sellerDto.setVerified(seller.getApprovedSeller() != null ? seller.getApprovedSeller() : false);
            sellerDto.setNurseryName(seller.getNurseryName());
            return sellerDto;
        });
    }

    @Transactional
    public void updateProduct(String username, int nurseryId, int productId, ProductDto productDto) {
        Nursery nursery = nurseryRepo.findById(nurseryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery does not exist"));
        if (!nursery.getOwner().getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden request");
        }
        
        InventoryProduct inventoryProduct = inventoryProductRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory product not found"));
                
        if (inventoryProduct.getInventory().getId() != nursery.getInventory().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product not in this nursery");
        }
        
        inventoryProduct.setCount(productDto.getStock());
        inventoryProductRepo.save(inventoryProduct);
        
        Product product = inventoryProduct.getProduct();
        product.setPrize(productDto.getPrize());
        productRepo.save(product);
    }
    
    @Transactional
    public void deleteProduct(String username, int nurseryId, int productId) {
        Nursery nursery = nurseryRepo.findById(nurseryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery does not exist"));
        if (!nursery.getOwner().getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden request");
        }
        
        InventoryProduct inventoryProduct = inventoryProductRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory product not found"));
                
        if (inventoryProduct.getInventory().getId() != nursery.getInventory().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product not in this nursery");
        }
        
        Product product = inventoryProduct.getProduct();
        inventoryProductRepo.delete(inventoryProduct);
        productRepo.delete(product);
    }
    
    public List<ProductPerformanceDto> getProductPerformance(String username, int nurseryId) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Nursery nursery = nurseryRepo.findById(nurseryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery not found"));
        
        if (nursery.getOwner().getId() != seller.getId()) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your nursery");
        }

        Map<Integer, ProductPerformanceDto> performanceMap = new HashMap<>();
        if (nursery.getInventory() != null && nursery.getInventory().getProductList() != null) {
            for (InventoryProduct invProd : nursery.getInventory().getProductList()) {
                ProductPerformanceDto dto = new ProductPerformanceDto();
                dto.setProductId(invProd.getProduct().getId());
                dto.setProductName(invProd.getProduct().getName());
                dto.setCurrentStock(invProd.getCount());
                dto.setTotalSoldQuantity(0);
                dto.setMonthlySoldQuantity(0);
                dto.setTotalRevenue(0.0);
                dto.setMonthlyRevenue(0.0);
                performanceMap.put(invProd.getProduct().getId(), dto);
            }
        }

        List<Order> orders = orderRepo.findByNursery(nursery);
        LocalDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        for (Order order : orders) {
            // Check if status means sold successfully (CREATED, etc.)
            // We'll count anything that is not EXPIRED or CANCELLED if you have those
            // For now, including all tracked complete orders:
            
            boolean isThisMonth = order.getCreatedAt() != null && !order.getCreatedAt().isBefore(firstDayOfMonth);
            
            if (order.getOrderProducts() != null) {
                for (OrderProduct op : order.getOrderProducts()) {
                    int pId = op.getProduct().getId();
                    ProductPerformanceDto dto = performanceMap.get(pId);
                    if (dto != null) {
                        dto.setTotalSoldQuantity(dto.getTotalSoldQuantity() + op.getCount());
                        dto.setTotalRevenue(dto.getTotalRevenue() + op.getPrize());
                        
                        if (isThisMonth) {
                            dto.setMonthlySoldQuantity(dto.getMonthlySoldQuantity() + op.getCount());
                            dto.setMonthlyRevenue(dto.getMonthlyRevenue() + op.getPrize());
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>(performanceMap.values());
    }

    public List<OrderResponse> getNurseryOrders(String username, int nurseryId) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Nursery nursery = nurseryRepo.findById(nurseryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery not found"));
        
        if (nursery.getOwner().getId() != seller.getId()) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your nursery");
        }
        
        List<Order> orders = orderRepo.findByNursery(nursery);
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(buildOrderResponseFromOrder(order));
        }
        return responses;
    }

    @Transactional
    public OrderResponse updateOrderStatus(String username, int nurseryId, Long orderId, OrderStatus newStatus) {
        User user = userRepo.findByUsernameAndRole(username, Role.VENDER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Seller seller = sellerRepo.findByUserAndIsApprovedSeller(user, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller not approved"));
        Nursery nursery = nurseryRepo.findById(nurseryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nursery not found"));
        
        if (nursery.getOwner().getId() != seller.getId()) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your nursery");
        }
        
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
                
        if (order.getNursery() == null || order.getNursery().getId() != nursery.getId()) {
             throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to this nursery");
        }
        
        order.setOrderStatus(newStatus);
        Order savedOrder = orderRepo.save(order);
        
        return buildOrderResponseFromOrder(savedOrder);
    }

    private OrderResponse buildOrderResponseFromOrder(Order order) {
        OrderResponse response = new OrderResponse();

        response.setRazorpayOrderId(order.getRazorpayOrderId());
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
