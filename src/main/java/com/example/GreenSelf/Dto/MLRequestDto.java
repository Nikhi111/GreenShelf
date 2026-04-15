package com.example.GreenSelf.Dto;

public class MLRequestDto {
    private double temperature;
    private int humidity;
    private String sunlight;

    public MLRequestDto(double temperature, int humidity, String sunlight) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.sunlight = sunlight;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getSunlight() {
        return sunlight;
    }

    public void setSunlight(String sunlight) {
        this.sunlight = sunlight;
    }
}
