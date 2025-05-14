package com.project.parksystem.service;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import com.project.parksystem.observer.OwnerNotifier;
import com.project.parksystem.observer.TaskEventManager;
import com.project.parksystem.repository.TaskJdbcRepository;
import com.project.parksystem.strategy.TaskStrategy;
import com.project.parksystem.strategy.TaskStrategyFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления задачами.
 */
@Service
public class TaskService {

    private final TaskJdbcRepository taskJdbcRepository;
    private final TaskStrategyFactory strategyFactory;
    private final TaskEventManager taskEventManager;
    private final UserService userService;
    private final PlantService plantService;

    @Autowired
    public TaskService(TaskJdbcRepository taskJdbcRepository,
                       TaskStrategyFactory strategyFactory,
                       TaskEventManager taskEventManager,
                       UserService userService,
                       PlantService plantService) {
        this.taskJdbcRepository = taskJdbcRepository;
        this.strategyFactory = strategyFactory;
        this.taskEventManager = taskEventManager;
        this.userService = userService;
        this.plantService = plantService;
    }

    /**
     * Инициализация подписчиков после создания бина.
     */
    @PostConstruct
    public void init() {
        taskEventManager.subscribe(new OwnerNotifier());
    }

    public List<Task> getAllTasks() {
        return taskJdbcRepository.findAll();
    }

    public List<Task> getTasksForForester(User forester) {
        return taskJdbcRepository.findByForester(forester.getId());
    }

    public Task getById(Long id) {
        return taskJdbcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    public void save(Task task) {
        taskJdbcRepository.save(task);
    }

    public void deleteById(Long id) {
        taskJdbcRepository.delete(id);
    }

    /**
     * Создает задачу на основе формы.
     */
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

        Plant plant = plantService.findByNameIgnoreCase(form.getPlantName())
                .orElseGet(() -> {
                    Plant newPlant = new Plant();
                    newPlant.setName(form.getPlantName());
                    return plantService.savePlant(newPlant);
                });

        task.setPlant(plant);

        taskJdbcRepository.save(task);
    }

    /**
     * Завершает задачу с использованием стратегии и отправкой уведомления.
     */
    public void completeTaskWithStrategyAndNotification(Long taskId, String reportText) {
        Task task = getById(taskId);

        task.setReportText(reportText);
        task.setStatus(Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());

        TaskStrategy strategy = strategyFactory.getStrategy(String.valueOf(task.getAction()));
        strategy.process(task);

        taskJdbcRepository.save(task);

        taskEventManager.notify(task);
    }

    /**
     * Проверяет существование растения по имени.
     */
    public boolean isValidPlant(String plantName) {
        return plantService.findByNameIgnoreCase(plantName).isPresent();
    }
}
