package com.project.parksystem.strategy;

import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Task;
import com.project.parksystem.service.PlantService;
import com.project.parksystem.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия посадки растений.
 */
@Component
public class PlantingStrategy implements TaskStrategy {

    private final TreeService treeService;
    private static final Logger log = LoggerFactory.getLogger(PlantingStrategy.class);

    public PlantingStrategy(TreeService treeService, PlantService plantService) {
        this.treeService = treeService;
    }

    @Override
    public void process(Task task) {

        log.info("🌱 Выполнение посадки...");

        // Растение уже должно быть загружено в Task
        Plant plant = task.getPlant();
        if (plant == null) {
            log.error("❌ В задаче нет растения! Посадка невозможна.");
            return;
        }

        // Проверка, что место свободно
        if (treeService.isPlaceOccupied(task.getCoordX(), task.getCoordY(), 60)) {
            log.warn("⚠ Место занято, посадка отменена.");
            return;
        }

        // Посадка дерева
        treeService.plantTree(plant, task.getCoordX(), task.getCoordY());

        log.info("🌳 Новое дерево '{}' посажено!", plant.getName());
    }

    @Override
    public String getSupportedAction() {
        return "PLANTING";
    }
}
