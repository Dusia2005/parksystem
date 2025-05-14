package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия тушения пожаров.
 */
@Component
public class FireExtinguishingStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FireExtinguishingStrategy.class);

    @Override
    public void process(Task task) {
        logger.info("Тушение пожара в зоне с координатами - " +
                task.getCoordX() + ", " + task.getCoordY());
    }

    @Override
    public String getSupportedAction() {
        return "FIRE_EXTINGUISHING";
    }
}
