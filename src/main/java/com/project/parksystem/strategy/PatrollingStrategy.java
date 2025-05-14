package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Стратегия патрулирования территории.
 */
@Component
public class PatrollingStrategy implements TaskStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PatrollingStrategy.class);

    /**
     * Обрабатывает задачу патрулирования.
     *
     * @param task задача, которую нужно выполнить
     */
    @Override
    public void process(Task task) {
        logger.info("Патрулирование области с координатами - " +
                task.getCoordX() + ", " + task.getCoordY());
    }

    /**
     * Возвращает действие, поддерживаемое данной стратегией.
     *
     * @return "PATROLLING"
     */
    @Override
    public String getSupportedAction() {
        return "PATROLLING";
    }
}
