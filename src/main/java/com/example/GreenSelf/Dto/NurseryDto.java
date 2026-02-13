package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.Address;
import com.example.GreenSelf.entity.Inventory;
import com.example.GreenSelf.entity.Seller;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public class NurseryDto{

    String name;
    String licenceNumber;
    Address address;

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
}
