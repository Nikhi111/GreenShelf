package com.example.GreenSelf.Dto;

public class ProductDetailedResponseDto {
    private int id;
    private String name;
    private String dsc;
    private String nurseryName;
    private String productImage;
    private int prize;
    
    // Plant entity properties
    private String scientificName;
    private String wateringFrequency;
    private String wateringDays;
    private String sunlight;
    private String careLevel;
    private boolean toxicToPets;
    private boolean toxicToHumans;
    private boolean indoorPlant;
    private String hardinessMin;
    private String hardinessMax;
    private String growthRate;
    private String soilType;
    private String floweringSeason;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDsc() { return dsc; }
    public void setDsc(String dsc) { this.dsc = dsc; }
    
    public String getNurseryName() { return nurseryName; }
    public void setNurseryName(String nurseryName) { this.nurseryName = nurseryName; }
    
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    
    public int getPrize() { return prize; }
    public void setPrize(int prize) { this.prize = prize; }

    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }
    
    public String getWateringFrequency() { return wateringFrequency; }
    public void setWateringFrequency(String wateringFrequency) { this.wateringFrequency = wateringFrequency; }
    
    public String getWateringDays() { return wateringDays; }
    public void setWateringDays(String wateringDays) { this.wateringDays = wateringDays; }
    
    public String getSunlight() { return sunlight; }
    public void setSunlight(String sunlight) { this.sunlight = sunlight; }
    
    public String getCareLevel() { return careLevel; }
    public void setCareLevel(String careLevel) { this.careLevel = careLevel; }
    
    public boolean isToxicToPets() { return toxicToPets; }
    public void setToxicToPets(boolean toxicToPets) { this.toxicToPets = toxicToPets; }
    
    public boolean isToxicToHumans() { return toxicToHumans; }
    public void setToxicToHumans(boolean toxicToHumans) { this.toxicToHumans = toxicToHumans; }
    
    public boolean isIndoorPlant() { return indoorPlant; }
    public void setIndoorPlant(boolean indoorPlant) { this.indoorPlant = indoorPlant; }
    
    public String getHardinessMin() { return hardinessMin; }
    public void setHardinessMin(String hardinessMin) { this.hardinessMin = hardinessMin; }
    
    public String getHardinessMax() { return hardinessMax; }
    public void setHardinessMax(String hardinessMax) { this.hardinessMax = hardinessMax; }
    
    public String getGrowthRate() { return growthRate; }
    public void setGrowthRate(String growthRate) { this.growthRate = growthRate; }
    
    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }
    
    public String getFloweringSeason() { return floweringSeason; }
    public void setFloweringSeason(String floweringSeason) { this.floweringSeason = floweringSeason; }
}
