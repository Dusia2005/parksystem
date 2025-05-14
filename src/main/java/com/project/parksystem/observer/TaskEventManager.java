package com.project.parksystem.observer;

import com.project.parksystem.model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер событий задач. Позволяет подписывать и уведомлять наблюдателей.
 */
@Component
public class TaskEventManager {

    private final List<TaskObserver> observers = new ArrayList<>();

    public void subscribe(TaskObserver observer) {
        observers.add(observer);
    }

    public void notify(Task task) {
        for (TaskObserver observer : observers) {
            observer.onTaskCompleted(task);
        }
    }
}
