package com.example.GreenSelf.Dto;

import java.util.List;

public class CartResponseDto {
    private List<CartItemDto> items;
    private int total;
    private int cartId;
    private String message;

    // Constructors
    public CartResponseDto() {}

    public CartResponseDto(List<CartItemDto> items, int total, int cartId) {
        this.items = items;
        this.total = total;
        this.cartId = cartId;
        this.message = "Cart retrieved successfully";
    }

    // Getters and Setters
    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
