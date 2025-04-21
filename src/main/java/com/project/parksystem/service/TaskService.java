package com.project.parksystem.service;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.User;
import com.project.parksystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksForForester(User forester) {
        return taskRepository.findByForester(forester);
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public void approveTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(Status.COMPLETED); // Или другой статус, если есть статус "APPROVED"
        taskRepository.save(task);
    }
    public boolean isValidPlant(String plantName) {
        List<String> allowedPlants = List.of(
                "ель", "сосна", "береза", "дуб", "липа", "елка",
                "роза", "тюльпаны", "ландыши", "лилия"
        );
        return allowedPlants.contains(plantName.toLowerCase());
    }

    public void createTaskFromForm(TaskForm form) {
        System.out.println(">>> action from form: " + form.getAction());

        Task task = new Task();
        task.setDescription(form.getDescription());
        task.setForester(userService.getUserById(form.getForesterId()));
        task.setStatus(Status.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setAction(form.getAction());

        Plant plant = new Plant();
        plant.setName(form.getPlantName());
        task.setPlant(plant);

        task.setCoordX(form.getCoordX());
        task.setCoordY(form.getCoordY());

        task.setApprovedByOwner(false);

        taskRepository.save(task);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    public void save(Task task) {
        taskRepository.save(task);
    }
}
