package com.project.parksystem.strategy;

import com.project.parksystem.model.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskStrategyFactory {

    private final Map<String, TaskStrategy> strategies = new HashMap<>();

    @Autowired
    public TaskStrategyFactory(List<TaskStrategy> strategyList) {
        for (TaskStrategy strategy : strategyList) {
            strategies.put(strategy.getSupportedAction(), strategy);
        }
    }

    public TaskStrategy getStrategy(String action) {
        TaskStrategy strategy = strategies.get(action);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for action: " + action);
        }
        return strategy;
    }
}

