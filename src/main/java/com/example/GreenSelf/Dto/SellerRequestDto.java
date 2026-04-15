package com.example.GreenSelf.Dto;

public class SellerRequestDto {
    private int sellerId;
    private boolean approved;

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}

