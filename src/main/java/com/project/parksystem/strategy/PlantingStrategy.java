package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия посадки растений.
 */
@Component
public class PlantingStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PlantingStrategy.class);

    /**
     * Обрабатывает задачу посадки растения.
     *
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void process(Task task) {
        logger.info("Посадка растения: " +
                (task.getPlant() != null ? task.getPlant().getName() : "неизвестное растение"));
    }

    /**
     * Возвращает действие, поддерживаемое данной стратегией.
     *
     * @return "PLANTING"
     */
    @Override
    public String getSupportedAction() {
        return "PLANTING";
    }
}
