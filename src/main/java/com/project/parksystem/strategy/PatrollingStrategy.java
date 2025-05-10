package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PatrollingStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PlantingStrategy.class);

    @Override
    public void process(Task task) {
        logger.info("Патрулирование области с координатами -  " + task.getCoordX() + ", " + task.getCoordY());
    }
    @Override
    public String getSupportedAction() {
        return "PATROLLING";
    }
}