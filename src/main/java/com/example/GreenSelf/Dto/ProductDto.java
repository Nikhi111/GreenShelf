package com.example.GreenSelf.Dto;

public class ProductDto {
    int  plantId;
    private  int prize;
    private  int stock;
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getPlantId() {
        return plantId;
    }
    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }
    public int getPrize() {
        return prize;
    }
    public void setPrize(int prize) {
        this.prize = prize;
    }

}
