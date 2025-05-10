package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WateringStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PlantingStrategy.class);

    @Override
    public void process(Task task) {
        logger.info("Полив растения: " + task.getPlant().getName());
    }
    @Override
    public String getSupportedAction() {
        return "WATERING";
    }
}
