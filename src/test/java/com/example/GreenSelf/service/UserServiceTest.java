package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.*;
import com.example.GreenSelf.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock UserRepo userRepo;
    @Mock CartRepo cartRepo;
    @Mock CartProductRepo cartProductRepo;
    @Mock ProductRepo productRepo;
    @Mock InventoryProductRepo inventoryProductRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock OrderRepo orderRepo;
    @Mock AddressRepo addressRepo;
    @Mock PaymentRepo paymentRepo;

    @InjectMocks
    UserService userService;

    private User mockUser;
    private Product mockProduct;
    private InventoryProduct mockInventory;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encoded_pass");
        mockUser.setRole(Role.USER);

        mockProduct = new Product();
        mockProduct.setId(1);
        mockProduct.setName("Monstera");
        mockProduct.setPrize(500);

        mockInventory = new InventoryProduct();
        mockInventory.setProduct(mockProduct);
        mockInventory.setCount(10);

        mockCart = new Cart();
        mockCart.setId(1);
        mockCart.setUser(mockUser);
        mockCart.setCartStatus(CartStatus.ACTIVE);
        mockCart.setTotalPrize(0);
        mockCart.setCartproductList(new ArrayList<>());
    }

    // ── createUser Tests ──────────────────────────────────────────────────────

    @Test
    @DisplayName("createUser: should create new user successfully")
    void createUser_success() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("newuser");
        dto.setPassword("password123");

        when(userRepo.findUserByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("hashed", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: should throw when username already exists")
    void createUser_duplicateUsername_throwsException() {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("testuser");
        dto.setPassword("password");

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.createUser(dto));
        assertTrue(ex.getMessage().contains("User already exists"));
        verify(userRepo, never()).save(any());
    }

    // ── addToCart Tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("addToCart: should add new product to empty cart")
    void addToCart_newProduct_createsCartAndAddsItem() {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(2);

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        when(cartProductRepo.save(any(CartProduct.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> userService.addToCart("testuser", dto));

        verify(cartRepo).save(any(Cart.class));
        verify(cartProductRepo).save(any(CartProduct.class));
        // Inventory should be decremented
        assertEquals(8, mockInventory.getCount());
    }

    @Test
    @DisplayName("addToCart: should increment count if product already in cart")
    void addToCart_existingProduct_incrementsCount() {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(1);

        CartProduct existingCp = new CartProduct();
        existingCp.setProduct(mockProduct);
        existingCp.setCount(3);
        existingCp.setPrizetotal(1500);
        mockCart.setCartproductList(new ArrayList<>(List.of(existingCp)));

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));
        when(cartProductRepo.save(any(CartProduct.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.addToCart("testuser", dto);

        assertEquals(4, existingCp.getCount()); // 3 + 1
        assertEquals(2000, existingCp.getPrizetotal()); // 500 * 4
    }

    @Test
    @DisplayName("addToCart: should throw when product exceeds max quantity (10)")
    void addToCart_exceedsMaxQuantity_throwsException() {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(5);

        CartProduct existingCp = new CartProduct();
        existingCp.setProduct(mockProduct);
        existingCp.setCount(8); // already 8, adding 5 = 13 > 10

        mockCart.setCartproductList(new ArrayList<>(List.of(existingCp)));

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addToCart("testuser", dto));
        assertTrue(ex.getMessage().contains("Maximum 10 items"));
    }

    @Test
    @DisplayName("addToCart: should throw when product is out of stock")
    void addToCart_outOfStock_throwsException() {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(15); // more than available (10)

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(1)).thenReturn(Optional.of(mockProduct));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addToCart("testuser", dto));
        assertEquals("Out of stock", ex.getMessage());
    }

    @Test
    @DisplayName("addToCart: should throw when product not found")
    void addToCart_productNotFound_throwsException() {
        CartDto dto = new CartDto();
        dto.setProductId(99);
        dto.setCount(1);

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(productRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addToCart("testuser", dto));
        assertEquals("Product not found", ex.getMessage());
    }

    // ── removeFromCart Tests ──────────────────────────────────────────────────

    @Test
    @DisplayName("removeFromCart: should remove item and restore inventory")
    void removeFromCart_success() {
        CartProduct cp = new CartProduct();
        cp.setProduct(mockProduct);
        cp.setCount(2);
        cp.setPrizetotal(1000);
        mockCart.setCartproductList(new ArrayList<>(List.of(cp)));

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> userService.removeFromCart("testuser", 1));

        assertEquals(0, mockCart.getCartproductList().size());
        assertEquals(12, mockInventory.getCount()); // 10 + 2 returned
        verify(cartRepo).save(mockCart);
    }

    @Test
    @DisplayName("removeFromCart: should throw when product not in cart")
    void removeFromCart_productNotInCart_throwsException() {
        mockCart.setCartproductList(new ArrayList<>());

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.removeFromCart("testuser", 99));
        assertEquals("Product not in cart", ex.getMessage());
    }

    // ── updateCartItemQuantity Tests ──────────────────────────────────────────

    @Test
    @DisplayName("updateCartItemQuantity: should update quantity within valid range")
    void updateCartItemQuantity_success() {
        CartProduct cp = new CartProduct();
        cp.setProduct(mockProduct);
        cp.setCount(3);
        cp.setPrizetotal(1500);
        mockCart.setCartproductList(new ArrayList<>(List.of(cp)));

        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));
        when(inventoryProductRepo.findByProduct(mockProduct)).thenReturn(Optional.of(mockInventory));
        when(cartProductRepo.save(any(CartProduct.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> userService.updateCartItemQuantity("testuser", 1, 5));

        assertEquals(5, cp.getCount());
        assertEquals(2500, cp.getPrizetotal()); // 500 * 5
    }

    @Test
    @DisplayName("updateCartItemQuantity: should throw when count is 0")
    void updateCartItemQuantity_zeroCount_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateCartItemQuantity("testuser", 1, 0));
        assertEquals("Quantity must be between 1 and 10", ex.getMessage());
    }

    @Test
    @DisplayName("updateCartItemQuantity: should throw when count exceeds 10")
    void updateCartItemQuantity_countAboveMax_throwsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateCartItemQuantity("testuser", 1, 11));
        assertEquals("Quantity must be between 1 and 10", ex.getMessage());
    }

    // ── getCart Tests ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getCart: should return active cart for user")
    void getCart_returnsActiveCart() {
        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.of(mockCart));

        Object result = userService.getCart("testuser");

        assertNotNull(result);
        assertInstanceOf(Cart.class, result);
        assertEquals(mockCart, result);
    }

    @Test
    @DisplayName("getCart: should return empty cart when no active cart exists")
    void getCart_noActiveCart_returnsEmptyCart() {
        when(userRepo.findUserByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(cartRepo.findByUserAndCartStatus(mockUser, CartStatus.ACTIVE)).thenReturn(Optional.empty());

        Object result = userService.getCart("testuser");

        assertNotNull(result);
        assertInstanceOf(Cart.class, result);
        assertEquals(0, ((Cart) result).getTotalPrize());
    }
}
