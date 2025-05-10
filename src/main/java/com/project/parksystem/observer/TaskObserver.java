package com.project.parksystem.observer;

import com.project.parksystem.model.Task;

public interface TaskObserver {
    void onTaskCompleted(Task task);
}
