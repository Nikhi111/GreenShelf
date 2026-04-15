package com.example.GreenSelf.controller;
import com.example.GreenSelf.Dto.PlantDto;
import com.example.GreenSelf.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/plants")
@Tag(name = "Plant Management", description = "Operations for managing plant catalog")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class PlantController {
        @Autowired
        private PlantService plantService;

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
        @Operation(summary = "Add a single plant", description = "Adds a new plant to the catalog")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Plant added successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Invalid plant data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Plant already exists", content = @Content)
        })
        public ResponseEntity<Map<String, String>> addPlant(
                        @Valid @RequestBody @Parameter(description = "Plant details") PlantDto plantDto) {

                plantService.addPlant(plantDto);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Plant added successfully");
                response.put("plantName", plantDto.getCommonName());

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/bulk")
        @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
        @Operation(summary = "Add multiple plants", description = "Bulk adds multiple plants to the catalog")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Plants added successfully", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "400", description = "Invalid plant data in list", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
                        @ApiResponse(responseCode = "207", description = "Partial success - some plants added", content = @Content)
        })
        public ResponseEntity<Map<String, Object>> addPlants(
                        @Valid @RequestBody @Parameter(description = "List of plant details") List<PlantDto> plantDtos) {

                plantService.addPlants(plantDtos);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Plants added successfully");
                response.put("count", plantDtos.size());

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'VENDER')")
        @Operation(summary = "Get all plants", description = "Retrieves all plants in the catalog")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Plants retrieved successfully", content = @Content(mediaType = "application/json"))
        })
        public ResponseEntity<Map<String, Object>> getAllPlants() {
                // Get all plants from service
                List<PlantDto> plants = plantService.getAllPlants();
                
                Map<String, Object> response = new HashMap<>();
                response.put("plants", plants);
                response.put("total", plants.size());
                response.put("message", "Plants retrieved successfully");

                return ResponseEntity.ok(response);
        }

        @GetMapping("/{plantId}")
        @Operation(summary = "Get plant by ID", description = "Retrieves detailed information about a specific plant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Plant found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "404", description = "Plant not found", content = @Content)
        })
        public ResponseEntity<Map<String, String>> getPlantById(
                        @PathVariable @Parameter(description = "Plant ID") Long plantId) {

                Map<String, String> response = new HashMap<>();
                response.put("message", "Implement getPlantById logic");

                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        @Operation(summary = "Search plants by name", description = "Retrieves a paginated list of plants matching the common or scientific name")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved matched plants",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
        })
        public ResponseEntity<Page<PlantDto>> searchPlantsByName(
                @RequestParam @Parameter(description = "Name of the plant to search for") String name,
                @Parameter(description = "Pagination parameters") Pageable pageable) {

                Page<PlantDto> plants = plantService.searchPlantsByName(name, pageable);
                return ResponseEntity.ok(plants);
        }
}