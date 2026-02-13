package com.example.GreenSelf.entity;
import jakarta.persistence.*;

@Entity
public class Nursery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String licenceNumber;
    @OneToOne
    Address address;
    @ManyToOne
    Seller owner;
    @OneToOne(mappedBy = "nursery")
    Inventory inventory;
    Boolean isVerified;
    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

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

    public Seller getOwner() {
        return owner;
    }

    public void setSeller(Seller owner) {
        this.owner = owner;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
