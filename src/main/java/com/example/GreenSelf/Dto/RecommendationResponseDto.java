package com.example.GreenSelf.Dto;

import java.util.List;

public class RecommendationResponseDto {
    private WeatherInfo weather;
    private List<PlantDto> plants;

    public WeatherInfo getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo weather) {
        this.weather = weather;
    }

    public List<PlantDto> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantDto> plants) {
        this.plants = plants;
    }

    public static class WeatherInfo {
        private double temperature;
        private int humidity;
        private String condition;

        public WeatherInfo(double temperature, int humidity, String condition) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.condition = condition;
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

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }
    }
}
