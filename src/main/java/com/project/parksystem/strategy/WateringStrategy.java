package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия полива растений.
 */
@Component
public class WateringStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(WateringStrategy.class);

    /**
     * Обрабатывает задачу полива.
     *
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void process(Task task) {
        logger.info("Полив растения: " +
                (task.getPlant() != null ? task.getPlant().getName() : "неизвестное растение"));
    }

    /**
     * Возвращает действие, поддерживаемое данной стратегией.
     *
     * @return "WATERING"
     */
    @Override
    public String getSupportedAction() {
        return "WATERING";
    }
}
