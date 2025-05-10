package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;

public interface TaskStrategy {
    void process(Task task);
    String getSupportedAction();
}

