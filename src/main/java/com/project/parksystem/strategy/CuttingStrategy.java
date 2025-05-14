package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия выполнения задачи по вырубке растений.
 */
@Component
public class CuttingStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CuttingStrategy.class);

    @Override
    public void process(Task task) {
        logger.info("Избавиться от растения: " +
                (task.getPlant() != null ? task.getPlant().getName() : "неизвестное растение"));
    }

    @Override
    public String getSupportedAction() {
        return "CUTTING";
    }
}
