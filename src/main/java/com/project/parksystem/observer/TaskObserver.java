package com.project.parksystem.observer;

import com.project.parksystem.model.Task;

/**
 * Интерфейс наблюдателя, реагирующего на завершение задачи.
 */
public interface TaskObserver {
    void onTaskCompleted(Task task);
}
