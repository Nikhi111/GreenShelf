package com.example.GreenSelf.repo;

import com.example.GreenSelf.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlantRepo extends JpaRepository<Plant,Integer>{
     Optional<Plant> findByPlantId(int plantId);
}
