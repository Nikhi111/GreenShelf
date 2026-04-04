package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.service.UserService;
import com.example.GreenSelf.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController MockMvc Tests")
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserService userService;
    @MockBean AuthenticationManager authenticationManager;
    @MockBean JwtUtil jwtUtil;
    @MockBean UserDetailsService userDetailsService;

    // ── POST /api/auth/login ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login: valid credentials should return 200 with token")
    void login_validCredentials_returns200() throws Exception {
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password", List.of()));
        when(jwtUtil.generateToken("testuser")).thenReturn("mock.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("Logged in successfully"));
    }

    @Test
    @DisplayName("POST /api/auth/login: wrong credentials should return 401")
    void login_wrongCredentials_returns401() throws Exception {
        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/user/register ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/user/register: valid data should return 201")
    void register_validData_returns201() throws Exception {
        com.example.GreenSelf.Dto.UserRequestDto dto = new com.example.GreenSelf.Dto.UserRequestDto();
        dto.setUsername("newuser");
        dto.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("newuser");

        when(userService.createUser(any())).thenReturn(savedUser);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    @DisplayName("POST /api/user/register: duplicate username returns 500 (RuntimeException)")
    void register_duplicateUsername_throws() throws Exception {
        com.example.GreenSelf.Dto.UserRequestDto dto = new com.example.GreenSelf.Dto.UserRequestDto();
        dto.setUsername("existing");
        dto.setPassword("password");

        when(userService.createUser(any())).thenThrow(new RuntimeException("User already exists: existing"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    // ── GET /api/user/me ──────────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("GET /api/user/me: authenticated user returns 200")
    void getMe_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/user/me: unauthenticated returns 401/403")
    void getMe_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().is4xxClientError());
    }

    // ── POST /api/user/cart ───────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("POST /api/user/cart: valid payload should add item and return 200")
    void addToCart_validPayload_returns200() throws Exception {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(2);

        doNothing().when(userService).addToCart(anyString(), any(CartDto.class));

        mockMvc.perform(post("/api/user/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item added to cart successfully"));

        verify(userService, times(1)).addToCart(eq("testuser"), any(CartDto.class));
    }

    @Test
    @DisplayName("POST /api/user/cart: unauthenticated should return 401/403")
    void addToCart_unauthenticated_returns401() throws Exception {
        CartDto dto = new CartDto();
        dto.setProductId(1);
        dto.setCount(1);

        mockMvc.perform(post("/api/user/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    // ── GET /api/user/cart ────────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("GET /api/user/cart: authenticated should return 200")
    void getCart_authenticated_returns200() throws Exception {
        when(userService.getCart("testuser")).thenReturn(new com.example.GreenSelf.entity.Cart());

        mockMvc.perform(get("/api/user/cart"))
                .andExpect(status().isOk());

        verify(userService).getCart("testuser");
    }

    // ── DELETE /api/user/cart/{itemId} ────────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("DELETE /api/user/cart/1: authenticated should remove item and return 200")
    void removeFromCart_authenticated_returns200() throws Exception {
        doNothing().when(userService).removeFromCart("testuser", 1);

        mockMvc.perform(delete("/api/user/cart/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item removed successfully"));

        verify(userService).removeFromCart("testuser", 1);
    }

    // ── PATCH /api/user/cart/{productId} ─────────────────────────────────────

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("PATCH /api/user/cart/1?count=5: should update quantity and return 200")
    void updateCartQuantity_validRequest_returns200() throws Exception {
        doNothing().when(userService).updateCartItemQuantity("testuser", 1, 5);

        mockMvc.perform(patch("/api/user/cart/1")
                        .param("count", "5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Quantity updated successfully"));

        verify(userService).updateCartItemQuantity("testuser", 1, 5);
    }

    // ── GET /api/public/home ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/public/home: public endpoint should return 200 without auth")
    void home_publicEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/api/public/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("GreenSelf")));
    }
}
