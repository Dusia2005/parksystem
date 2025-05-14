package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;

/**
 * Интерфейс стратегии выполнения задач.
 */
public interface TaskStrategy {

    /**
     * Выполняет обработку задачи.
     *
     * @param task задача для выполнения
     */
    void process(Task task);

    /**
     * Возвращает строку, идентифицирующую поддерживаемое действие.
     *
     * @return название действия
     */
    String getSupportedAction();
}
