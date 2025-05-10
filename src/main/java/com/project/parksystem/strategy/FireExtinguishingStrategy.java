package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.LinkOption;

@Component
public class FireExtinguishingStrategy implements TaskStrategy {
    private static final Logger logger = LoggerFactory.getLogger(PlantingStrategy.class);

    @Override
    public void process(Task task) {
        logger.info("Тушение пожара в зоне с координатами - " + task.getCoordX() + ", " + task.getCoordY());
    }
    @Override
    public String getSupportedAction() {
        return "FIRE_EXTINGUISHING";
    }
}
