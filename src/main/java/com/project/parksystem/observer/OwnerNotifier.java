package com.project.parksystem.observer;

import com.project.parksystem.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Наблюдатель, уведомляющий владельца о завершении задачи.
 */
public class OwnerNotifier implements TaskObserver {

    private static final Logger logger = LogManager.getLogger(OwnerNotifier.class);

    @Override
    public void onTaskCompleted(Task task) {
        logger.info("Уведомление: Задача №{} завершена. Уведомлён владелец.", task.getId());
    }
}
