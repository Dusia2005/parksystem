package com.project.parksystem.service;

import com.project.parksystem.model.Plant;
import com.project.parksystem.repository.PlantJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой для работы с растениями.
 */
@Service
public class PlantService {

    private final PlantJdbcRepository plantJdbcRepository;
    private final PlantFileService plantFileService; // добавляем

    public PlantService(PlantJdbcRepository plantDao, PlantFileService plantFileService) {
        this.plantJdbcRepository = plantDao;
        this.plantFileService = plantFileService;
    }
    /**
     * Получить список всех растений.
     */
    public List<Plant> findAll() {
        try {
            return plantJdbcRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить список растений", e);
        }
    }

    /**
     * Найти растение по ID.
     */
    public Plant findById(Long id) {
        try {
            return plantJdbcRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Растение не найдено"));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске растения по id", e);
        }
    }

    /**
     * Найти растение по имени без учета регистра.
     */
    public Optional<Plant> findByNameIgnoreCase(String name) {
        try {
            return plantJdbcRepository.findByNameIgnoreCase(name);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске по имени", e);
        }
    }

    /**
     * Проверить, существует ли растение с таким именем.
     */
    public boolean existsByNameIgnoreCase(String name) {
        try {
            return plantJdbcRepository.existsByNameIgnoreCase(name);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке существования растения", e);
        }
    }

    /**
     * Сохранить растение.
     */
    public Plant savePlant(Plant plant) {
        try {
            plantJdbcRepository.save(plant);
            return plant;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении растения", e);
        }
    }

    /**
     * Создать растение, если с таким именем еще не существует.
     */
    public Plant createPlantIfNotExists(String name) {
        if (existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Растение с таким именем уже существует!");
        }
        Plant plant = new Plant();
        plant.setName(name);
        return savePlant(plant);
    }

    /**
     * Удалить растение по ID.
     */
    public void deleteById(Long id) {
        try {
            // Получаем растение (если есть) чтобы узнать имя файла
            Optional<Plant> maybe = plantJdbcRepository.findById(id);
            if (maybe.isPresent()) {
                Plant p = maybe.get();
                if (p.getImageFilename() != null) {
                    plantFileService.delete(p.getImageFilename());
                }
            }
            plantJdbcRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении растения", e);
        }
    }
}
