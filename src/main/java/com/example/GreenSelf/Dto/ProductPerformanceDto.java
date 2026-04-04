package com.example.GreenSelf.Dto;

public class ProductPerformanceDto {
    private int productId;
    private String productName;
    private int currentStock;
    private int totalSoldQuantity;
    private int monthlySoldQuantity;
    private double totalRevenue;
    private double monthlyRevenue;
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    
    public int getTotalSoldQuantity() { return totalSoldQuantity; }
    public void setTotalSoldQuantity(int totalSoldQuantity) { this.totalSoldQuantity = totalSoldQuantity; }
    
    public int getMonthlySoldQuantity() { return monthlySoldQuantity; }
    public void setMonthlySoldQuantity(int monthlySoldQuantity) { this.monthlySoldQuantity = monthlySoldQuantity; }
    
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public double getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(double monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
}
