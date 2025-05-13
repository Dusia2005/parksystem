package com.project.parksystem.factory;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Task;
import com.project.parksystem.repository.PlantJdbcRepository;
import com.project.parksystem.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        task.setForester(userRepository.findById(form.getForesterId()).orElseThrow());
        task.setPlant(plantRepository.findByNameIgnoreCase(form.getPlantName()).orElseThrow());
        return task;
    }
}
