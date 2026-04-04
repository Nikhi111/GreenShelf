package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.Seller;

public class SellerResponseDto {
    int id;
    String name;
    String nurseryName;
    boolean isVerified;

    public SellerResponseDto(int id, String name, String nurseryName, boolean isVerified) {
        this.id = id;
        this.name = name;
        this.nurseryName = nurseryName;
        this.isVerified = isVerified;
    }
    public SellerResponseDto(Seller seller){
        this.id=seller.getId();
        this.name=seller.getUser().getUsername();
        this.nurseryName=seller.getNurseryName();
        this.isVerified=seller.isApprovedSeller();
    }
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNurseryName() {
        return nurseryName;
    }

    public void setNurseryName(String nurseryName) {
        this.nurseryName = nurseryName;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
