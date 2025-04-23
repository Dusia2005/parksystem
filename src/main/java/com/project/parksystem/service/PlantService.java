package com.project.parksystem.service;

import com.project.parksystem.model.Plant;
import com.project.parksystem.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Optional<Plant> findByNameIgnoreCase(String name) {
        return plantRepository.findByNameIgnoreCase(name);
    }
    public Plant savePlant(Plant plant) {
        return plantRepository.save(plant);
    }

    public boolean existsByNameIgnoreCase(String name) {
        return plantRepository.existsByNameIgnoreCase(name);
    }

    public Plant createPlantIfNotExists(String name) {
        if (existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Растение с таким именем уже существует!");
        }

        Plant plant = new Plant();
        plant.setName(name);
        return plantRepository.save(plant);
    }
    public List<Plant> findAll() {
        return plantRepository.findAll();
    }

    public Plant findById(Long id) {
        return plantRepository.findById(id).orElseThrow(() -> new RuntimeException("Растение не найдено"));
    }

    public void deleteById(Long id) {
        plantRepository.deleteById(id);
    }
}
