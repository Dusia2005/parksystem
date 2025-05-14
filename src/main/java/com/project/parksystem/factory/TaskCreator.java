package com.project.parksystem.factory;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Task;

/**
 * Интерфейс для фабрик по созданию задач.
 */
public interface TaskCreator {
    Task createTask(TaskForm form);
}
