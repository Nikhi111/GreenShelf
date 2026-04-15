package com.example.GreenSelf.Dto;

public class SellerDto {
    private int id;
    private String name;
    private String nurseryName;
    private boolean isVerified;
    private String username;

    public int getId() {
        return id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


