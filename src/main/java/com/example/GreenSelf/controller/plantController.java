package com.example.GreenSelf.controller;

import com.example.GreenSelf.Dto.PlantDto;
import com.example.GreenSelf.entity.Plant;
import com.example.GreenSelf.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class plantController {
    @Autowired
    PlantService plantService;
    @PostMapping("/plant")
    void addPlant(@RequestBody PlantDto plantDto){
        plantService.addPlant(plantDto);
    }
    @PostMapping("/plants")
    void addPlants(@RequestBody List<PlantDto> plantDtos){
        plantService.addPlants(plantDtos);
    }
}
