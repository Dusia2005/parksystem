package com.project.parksystem.factory;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Task;
import com.project.parksystem.repository.PlantJdbcRepository;
import com.project.parksystem.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Реализация TaskCreator — создает задачи из формы, используя данные из репозиториев.
 */
@Component
@RequiredArgsConstructor
public class DefaultTaskCreator implements TaskCreator {

    private final UserJdbcRepository userRepository;
    private final PlantJdbcRepository plantRepository;

    @Override
    public Task createTask(TaskForm form) {
        Task task = new Task();
        task.setDescription(form.getDescription());
        task.setAction(form.getAction());
        task.setCoordX(form.getCoordX());
        task.setCoordY(form.getCoordY());
        task.setForester(userRepository.findById(form.getForesterId())
                .orElseThrow(() -> new IllegalArgumentException("Лесник не найден")));
        task.setPlant(plantRepository.findByNameIgnoreCase(form.getPlantName())
                .orElseThrow(() -> new IllegalArgumentException("Растение не найдено")));
        return task;
    }
}
