package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.OrderResponse;
import com.example.GreenSelf.entity.OrderStatus;
import com.example.GreenSelf.service.SellerService;
import com.example.GreenSelf.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
@DisplayName("SellerController MockMvc Tests")
public class SellerControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean SellerService sellerService;
    @MockBean UserDetailsService userDetailsService;
    @MockBean JwtUtil jwtUtil;

    @Test
    @WithMockUser(username = "testSeller", roles = {"SELLER"})
    @DisplayName("PATCH /api/seller/nursery/{nurseryId}/orders/{id}/status: Updates order status")
    void updateOrderStatus_Returns200() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setOrderId(10L);
        response.setOrderStatus(OrderStatus.DELIVERED);
        response.setTotalPrice(500.0);

        when(sellerService.updateOrderStatus(eq("testSeller"), eq(5), eq(10L), eq(OrderStatus.DELIVERED)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/seller/nursery/5/orders/10/status")
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.orderStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.totalPrice").value(500.0));
    }
}
