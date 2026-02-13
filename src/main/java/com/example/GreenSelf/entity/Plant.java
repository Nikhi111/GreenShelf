package com.example.GreenSelf.entity;

import jakarta.persistence.*;

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private int plantId;
    private String commonName;
    private String scientificName;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
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
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(String wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public String getWateringDays() {
        return wateringDays;
    }

    public void setWateringDays(String wateringDays) {
        this.wateringDays = wateringDays;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public boolean isToxicToPets() {
        return toxicToPets;
    }

    public void setToxicToPets(boolean toxicToPets) {
        this.toxicToPets = toxicToPets;
    }

    public boolean isToxicToHumans() {
        return toxicToHumans;
    }

    public void setToxicToHumans(boolean toxicToHumans) {
        this.toxicToHumans = toxicToHumans;
    }

    public boolean isIndoorPlant() {
        return indoorPlant;
    }

    public void setIndoorPlant(boolean indoorPlant) {
        this.indoorPlant = indoorPlant;
    }

    public String getHardinessMin() {
        return hardinessMin;
    }

    public void setHardinessMin(String hardinessMin) {
        this.hardinessMin = hardinessMin;
    }

    public String getHardinessMax() {
        return hardinessMax;
    }

    public void setHardinessMax(String hardinessMax) {
        this.hardinessMax = hardinessMax;
    }

    public String getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(String growthRate) {
        this.growthRate = growthRate;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getFloweringSeason() {
        return floweringSeason;
    }

    public void setFloweringSeason(String floweringSeason) {
        this.floweringSeason = floweringSeason;
    }
}
