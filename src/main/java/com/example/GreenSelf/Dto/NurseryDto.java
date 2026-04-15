package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.Address;
import com.example.GreenSelf.entity.Inventory;
import com.example.GreenSelf.entity.Seller;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public class NurseryDto{
    private int id;
    String name;
    String licenceNumber;
    Address address;
    boolean verified;

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

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
