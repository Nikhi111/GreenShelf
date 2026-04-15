package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.*;
import com.example.GreenSelf.entity.Plant;
import com.example.GreenSelf.repo.PlantRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlantRepo plantRepo;

    private final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}&units=metric";
    private final String ML_API_URL = "https://indoor-plant-api-1.onrender.com/predict";
    private final String API_KEY = "ec8a92a497452b7731c15b3f2836f9f9";

    public RecommendationResponseDto recommendPlants(double lat, double lon) {
        // 1. Get Weather Data
        WeatherResponseDto weatherResponse = fetchWeatherData(lat, lon);
        
        double temp = weatherResponse.getMain().getTemp();
        int humidity = weatherResponse.getMain().getHumidity();
        String condition = weatherResponse.getWeather().get(0).getMain();
        
        // 2. Map Weather to sunlight
        String mappedSunlight = mapWeatherToSunlight(condition);
        logger.info("Mapped weather condition '{}' to sunlight '{}'", condition, mappedSunlight);

        // 3. Call ML API
        MLResponseDto mlResponse = fetchMLRecommendations(temp, humidity, mappedSunlight);

        // 4. Fetch details from DB and limit to top 5
        List<PlantDto> plantDetails = new ArrayList<>();
        if (mlResponse.getRecommendedPlants() != null) {
            List<String> plantNames = mlResponse.getRecommendedPlants().stream()
                    .map(MLResponseDto.PlantName::getCommonName)
                    .limit(12)
                    .collect(Collectors.toList());

            for (String name : plantNames) {
                List<Plant> matchingPlants = plantRepo.findByCommonNameIgnoreCase(name);
                if (!matchingPlants.isEmpty()) {
                    for (Plant plant : matchingPlants) {
                        plantDetails.add(mapToDto(plant));
                    }
                } else {
                    logger.warn("Recommended plant '{}' not found in database", name);
                }
            }
        }

        // 5. Construct Final Response
        RecommendationResponseDto response = new RecommendationResponseDto();
        response.setWeather(new RecommendationResponseDto.WeatherInfo(temp, humidity, condition));
        response.setPlants(plantDetails);
        
        return response;
    }

    private WeatherResponseDto fetchWeatherData(double lat, double lon) {
        try {
            logger.info("Fetching weather data for lat: {}, lon: {}", lat, lon);
            return restTemplate.getForObject(WEATHER_API_URL, WeatherResponseDto.class, lat, lon, API_KEY);
        } catch (Exception e) {
            logger.error("Error fetching weather data: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch weather data from external API");
        }
    }

    private MLResponseDto fetchMLRecommendations(double temp, int humidity, String sunlight) {
        try {
            logger.info("Requesting ML recommendations for temp: {}, humidity: {}, sunlight: {}", temp, humidity, sunlight);
            MLRequestDto request = new MLRequestDto(temp, humidity, sunlight);
            return restTemplate.postForObject(ML_API_URL, request, MLResponseDto.class);
        } catch (Exception e) {
            logger.error("Error calling ML API: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get recommendations from ML model (Service might be down or timed out)");
        }
    }

    private String mapWeatherToSunlight(String condition) {
        if (condition == null) return "partial shade";
        switch (condition.toLowerCase()) {
            case "clear": return "full sun";
            case "clouds": return "partial shade";
            case "rain":
            case "drizzle":
            case "thunderstorm": return "low light";
            default: return "partial shade";
        }
    }

    private PlantDto mapToDto(Plant plant) {
        PlantDto dto = new PlantDto();
        dto.setId(plant.getId());
        dto.setPlantId(plant.getPlantId());
        dto.setCommonName(plant.getCommonName());
        dto.setScientificName(plant.getScientificName());
        dto.setDescription(plant.getDescription());
        dto.setImageUrl(plant.getImageUrl());
        dto.setWateringFrequency(plant.getWateringFrequency());
        dto.setWateringDays(plant.getWateringDays());
        dto.setSunlight(plant.getSunlight());
        dto.setCareLevel(plant.getCareLevel());
        dto.setToxicToPets(plant.isToxicToPets());
        dto.setToxicToHumans(plant.isToxicToHumans());
        dto.setIndoorPlant(plant.isIndoorPlant());
        dto.setHardinessMin(plant.getHardinessMin());
        dto.setHardinessMax(plant.getHardinessMax());
        dto.setGrowthRate(plant.getGrowthRate());
        dto.setSoilType(plant.getSoilType());
        dto.setFloweringSeason(plant.getFloweringSeason());
        return dto;
    }
}
