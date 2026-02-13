package com.example.GreenSelf.service;

import com.example.GreenSelf.Dto.PlantDto;
import com.example.GreenSelf.entity.Plant;
import com.example.GreenSelf.repo.PlantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class PlantService {
    @Autowired
    PlantRepo plantRepo;
    public void addPlants(List<PlantDto> plantDtos) {
        for (PlantDto dto: plantDtos){
            addPlant(dto);
        }
    }
    public void addPlant(PlantDto dto) {
        if(plantRepo.findByPlantId(dto.getPlantId()).isPresent()) new ResponseStatusException(HttpStatus.CONFLICT,"plant already exist");
        Plant plant=new Plant();
        plant.setPlantId(dto.getPlantId());
        plant.setCommonName(dto.getCommonName());
        plant.setScientificName(dto.getScientificName());
        plant.setDescription(dto.getDescription());
        plant.setImageUrl(dto.getImageUrl());
        plant.setWateringFrequency(dto.getWateringFrequency());
        plant.setWateringDays(dto.getWateringDays());
        plant.setSunlight(dto.getSunlight());
        plant.setCareLevel(dto.getCareLevel());
        plant.setToxicToPets(dto.isToxicToPets());
        plant.setToxicToHumans(dto.isToxicToHumans());
        plant.setIndoorPlant(dto.isIndoorPlant());
        plant.setHardinessMin(dto.getHardinessMin());
        plant.setHardinessMax(dto.getHardinessMax());
        plant.setGrowthRate(dto.getGrowthRate());
        plant.setSoilType(dto.getSoilType());
        plant.setFloweringSeason(dto.getFloweringSeason());
        plantRepo.save(plant);
    }
}
