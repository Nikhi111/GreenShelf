package com.example.GreenSelf.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MLResponseDto {
    @JsonProperty("recommended_plants")
    private List<PlantName> recommendedPlants;

    public List<PlantName> getRecommendedPlants() {
        return recommendedPlants;
    }

    public void setRecommendedPlants(List<PlantName> recommendedPlants) {
        this.recommendedPlants = recommendedPlants;
    }

    public static class PlantName {
        @JsonProperty("common_name")
        private String commonName;

        public String getCommonName() {
            return commonName;
        }

        public void setCommonName(String commonName) {
            this.commonName = commonName;
        }
    }
}
