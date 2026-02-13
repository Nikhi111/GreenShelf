package com.example.GreenSelf.Dto;

public class ProductResponseDto {
   private int id;
    String name;
    String dsc;
    String NurseryName;
    String productImage;
    int prize;
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
    public String getDsc() {
        return dsc;
    }
    public void setDsc(String dsc) {
        this.dsc = dsc;
    }
    public String getNurseryName() {
        return NurseryName;
    }

    public void setNurseryName(String nurseryName) {
        NurseryName = nurseryName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }
}
