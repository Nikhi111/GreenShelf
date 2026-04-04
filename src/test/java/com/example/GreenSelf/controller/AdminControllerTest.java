package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.SellerDto;
import com.example.GreenSelf.service.AdminService;
import com.example.GreenSelf.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void searchSellerByUsername_Success() throws Exception {
        // Arrange
        SellerDto mockSellerDto = new SellerDto();
        mockSellerDto.setId(5);
        mockSellerDto.setName("Test User");
        mockSellerDto.setNurseryName("Test Nursery");
        mockSellerDto.setVerified(true);
        
        Page<SellerDto> mockPage = new PageImpl<>(Collections.singletonList(mockSellerDto));
        
        when(sellerService.searchSellerByUsername(eq("Test"), any(Pageable.class))).thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/admin/search-seller")
                .param("username", "Test")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5))
                .andExpect(jsonPath("$.content[0].name").value("Test User"))
                .andExpect(jsonPath("$.content[0].nurseryName").value("Test Nursery"))
                .andExpect(jsonPath("$.content[0].verified").value(true));
    }
}
