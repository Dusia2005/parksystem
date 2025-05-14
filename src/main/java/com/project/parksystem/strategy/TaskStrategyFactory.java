package com.project.parksystem.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фабрика для получения нужной стратегии по действию.
 */
@Component
public class TaskStrategyFactory {

    private final Map<String, TaskStrategy> strategies = new HashMap<>();

    /**
     * Конструктор, автоматически внедряет все реализации TaskStrategy.
     *
     * @param strategyList список стратегий, внедрённых Spring
     */
    @Autowired
    public TaskStrategyFactory(List<TaskStrategy> strategyList) {
        for (TaskStrategy strategy : strategyList) {
            strategies.put(strategy.getSupportedAction(), strategy);
        }
    }

    /**
     * Получает стратегию на основе действия.
     *
     * @param action действие задачи
     * @return соответствующая стратегия
     * @throws IllegalArgumentException если стратегия не найдена
     */
    public TaskStrategy getStrategy(String action) {
        TaskStrategy strategy = strategies.get(action);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for action: " + action);
        }
        return strategy;
    }
}
