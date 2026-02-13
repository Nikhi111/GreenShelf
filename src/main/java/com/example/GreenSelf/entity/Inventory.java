package com.example.GreenSelf.entity;
import jakarta.persistence.*;
import java.util.List;
@Entity
public class Inventory{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
    @OneToMany(mappedBy = "inventory")
    List<InventoryProduct> productList;
    @OneToOne
    Nursery nursery;
    @OneToMany(
            mappedBy = "inventory",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    public List<InventoryProduct> getProductList() {
        return productList;
    }
    public void setProductList(List<InventoryProduct> productList) {
        this.productList = productList;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Nursery getNursery() {
        return nursery;
    }
    public void setNursery(Nursery nursery) {
        this.nursery = nursery;
    }
}
