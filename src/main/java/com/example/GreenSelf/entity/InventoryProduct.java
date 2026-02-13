package com.example.GreenSelf.entity;

import jakarta.persistence.*;

@Entity
public class InventoryProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private  int count;
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
