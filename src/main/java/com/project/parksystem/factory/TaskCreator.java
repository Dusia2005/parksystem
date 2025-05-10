package com.project.parksystem.factory;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Task;

public interface TaskCreator {
    Task createTask(TaskForm form);
}
