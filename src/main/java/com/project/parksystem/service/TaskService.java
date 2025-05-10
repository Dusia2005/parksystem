package com.project.parksystem.service;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.User;
import com.project.parksystem.observer.OwnerNotifier;
import com.project.parksystem.observer.TaskEventManager;
import com.project.parksystem.repository.TaskRepository;
import com.project.parksystem.strategy.TaskStrategy;
import com.project.parksystem.strategy.TaskStrategyFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskStrategyFactory strategyFactory;
    private final TaskEventManager taskEventManager;
    private final UserService userService;
    private final PlantService plantService;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskStrategyFactory strategyFactory,
                       TaskEventManager taskEventManager,
                       UserService userService,
                       PlantService plantService) {
        this.taskRepository = taskRepository;
        this.strategyFactory = strategyFactory;
        this.taskEventManager = taskEventManager;
        this.userService = userService;
        this.plantService = plantService;
    }


    @PostConstruct
    public void init() {
        taskEventManager.subscribe(new OwnerNotifier()); // или инжекти его, если он @Component
    }

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
        return plantService.findByNameIgnoreCase(plantName).isPresent();
    }

    public void createTaskFromForm(TaskForm form) {
        Task task = new Task();
        task.setDescription(form.getDescription());
        task.setForester(userService.getUserById(form.getForesterId()));
        task.setStatus(Status.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setAction(form.getAction());
        task.setCoordX(form.getCoordX());
        task.setCoordY(form.getCoordY());
        task.setApprovedByOwner(false);

        // 🌱 Найти растение по имени (игнорируя регистр), или создать новое
        Plant plant = plantService.findByNameIgnoreCase(form.getPlantName())
                .orElseGet(() -> {
                    Plant newPlant = new Plant();
                    newPlant.setName(form.getPlantName());
                    return plantService.savePlant(newPlant); // сохраняем и возвращаем
                });

        task.setPlant(plant);

        taskRepository.save(task);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    public void save(Task task) {
        taskRepository.save(task);
    }
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена с id: " + id));
    }
    public void deleteById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
    }

    public void completeTaskWithStrategyAndNotification(Long taskId, String reportText) {
        Task task = getById(taskId);

        task.setReportText(reportText);
        task.setStatus(Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());

        TaskStrategy strategy = strategyFactory.getStrategy(String.valueOf(task.getAction()));
        strategy.process(task);

        taskRepository.save(task);

        taskEventManager.notify(task);
    }
}
