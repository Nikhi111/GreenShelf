package com.example.GreenSelf.Dto;
public class CartDto {
   private int productId;
   private  int count;
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
