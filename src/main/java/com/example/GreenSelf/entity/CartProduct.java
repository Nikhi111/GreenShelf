package com.example.GreenSelf.entity;
import jakarta.persistence.*;

@Entity
public class CartProduct{
    @Id
    @GeneratedValue
    int id;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    Cart cart;
    @ManyToOne
    Product product;
    int prizetotal;
    int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getPrizetotal() {
        return prizetotal;
    }

    public void setPrizetotal(int prizetotal) {
        this.prizetotal = prizetotal;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
