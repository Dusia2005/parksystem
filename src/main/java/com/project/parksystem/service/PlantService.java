
/**
 * Сервисный слой для работы с растениями.
 */
package com.project.parksystem.service;

import com.project.parksystem.model.Plant;
import com.project.parksystem.repository.PlantJdbcRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@Service
public class PlantService {

    private final PlantJdbcRepository plantJdbcRepository;

    public PlantService(PlantJdbcRepository plantJdbcRepository) {
        this.plantJdbcRepository = plantJdbcRepository;
    }

    // Получить все растения
    public List<Plant> findAll() {
        return plantJdbcRepository.findAll();
    }

    // Найти по ID
    public Plant findById(Long id) {
        return plantJdbcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Растение не найдено"));
    }

    // Найти по имени без учета регистра
    public Optional<Plant> findByNameIgnoreCase(String name) {
        return plantJdbcRepository.findByNameIgnoreCase(name);
    }

    // Проверка существования
    public boolean existsByNameIgnoreCase(String name) {
        return plantJdbcRepository.existsByNameIgnoreCase(name);
    }

    // Сохранить
    public Plant savePlant(Plant plant) {
        plantJdbcRepository.save(plant);
        return plant;
    }

    // Обновить (без файлов)
    public Plant updatePlant(Plant plant) {
        plantJdbcRepository.update(plant);
        return plant;
    }

    // Удалить
    public void deleteById(Long id) {
        plantJdbcRepository.deleteById(id);
    }

    // 📌 Получить список доступных изображений из /static/assets/plants
    public List<String> getAvailableImages() {
        try {
            Path path = Paths.get("src/main/resources/static/assets/plants");
            return Files.list(path)
                    .filter(Files::isRegularFile)
                    .map(f -> f.getFileName().toString())
                    .filter(name -> name.endsWith(".png") ||
                            name.endsWith(".jpg") ||
                            name.endsWith(".jpeg") ||
                            name.endsWith(".webp"))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать папку с изображениями", e);
        }
    }
}
