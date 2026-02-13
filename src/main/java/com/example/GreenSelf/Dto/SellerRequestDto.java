package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.SellerRequestStatus;

public class SellerRequestDto {
    int id;
    boolean isVerified;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
