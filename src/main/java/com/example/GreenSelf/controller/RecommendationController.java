package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.RecommendationResponseDto;
import com.example.GreenSelf.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@Tag(name = "Plant Recommendation", description = "AI-powered plant recommendations based on real-time weather data")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/plants")
    @Operation(
            summary = "Get plant recommendations",
            description = "Analyzes real-time weather at the given coordinates and returns the best indoor plants using an AI model."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recommendations"),
            @ApiResponse(responseCode = "500", description = "Internal server error from external APIs or database")
    })
    public ResponseEntity<RecommendationResponseDto> getRecommendations(
            @RequestParam @Parameter(description = "Latitude of the location", example = "18.52") Double lat,
            @RequestParam @Parameter(description = "Longitude of the location", example = "73.85") Double lon) {

        RecommendationResponseDto recommendations = recommendationService.recommendPlants(lat, lon);
        return ResponseEntity.ok(recommendations);
    }
}
