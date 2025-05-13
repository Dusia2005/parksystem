package com.project.parksystem.service;

import com.project.parksystem.model.Plant;
import com.project.parksystem.repository.PlantJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    private final PlantJdbcRepository plantJdbcRepository;

    public PlantService(PlantJdbcRepository plantDao) {
        this.plantJdbcRepository = plantDao;
    }

    public List<Plant> findAll() {
        try {
            return plantJdbcRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить список растений", e);
        }
    }

    public Plant findById(Long id) {
        try {
            return plantJdbcRepository.findById(id).orElseThrow(() -> new RuntimeException("Растение не найдено"));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске растения по id", e);
        }
    }

    public Optional<Plant> findByNameIgnoreCase(String name) {
        try {
            return plantJdbcRepository.findByNameIgnoreCase(name);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске по имени", e);
        }
    }

    public boolean existsByNameIgnoreCase(String name) {
        try {
            return plantJdbcRepository.existsByNameIgnoreCase(name);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке существования растения", e);
        }
    }

    public Plant savePlant(Plant plant) {
        try {
            plantJdbcRepository.save(plant);
            return plant;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении растения", e);
        }
    }

    public Plant createPlantIfNotExists(String name) {
        if (existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Растение с таким именем уже существует!");
        }
        Plant plant = new Plant();
        plant.setName(name);
        return savePlant(plant);
    }

    public void deleteById(Long id) {
        try {
            plantJdbcRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении растения", e);
        }
    }
}
