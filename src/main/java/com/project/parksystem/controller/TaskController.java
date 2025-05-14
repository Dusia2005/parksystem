package com.project.parksystem.controller;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import com.project.parksystem.service.PlantService;
import com.project.parksystem.service.StatisticsService;
import com.project.parksystem.service.TaskService;
import com.project.parksystem.service.UserService;
import com.project.parksystem.strategy.TaskStrategyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления задачами (создание, одобрение, выполнение, история и т.д.).
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private TaskStrategyFactory strategyFactory;

    private final StatisticsService statisticsService;

    @Autowired
    public TaskController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Получение списка задач, ожидающих одобрения владельцем.
     */
    @GetMapping("/owner-tasks")
    public String listTasks(Model model) {
        logger.info("Загрузка задач для владельца");

        List<Task> tasks = taskService.getAllTasks().stream()
                .filter(task -> !task.isApprovedByOwner())
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        return "owner-tasks";
    }

    /**
     * Одобрение задачи владельцем.
     */
    @PostMapping("/approve/{taskId}")
    public String approveTask(@PathVariable Long taskId) {
        logger.info("Одобрение задачи ID {}", taskId);

        Task task = taskService.getById(taskId);
        task.setApprovedByOwner(true);
        task.setCompletedByOwnerAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);

        return "redirect:/tasks/owner-tasks";
    }

    /**
     * Отображение формы создания новой задачи.
     */
    @GetMapping("/new")
    public String showCreateTaskForm(Model model) {
        logger.info("Открытие формы создания задачи");

        model.addAttribute("task", new TaskForm());
        model.addAttribute("foresters", userService.getAllForesters());
        model.addAttribute("allowedPlants", plantService.findAll());

        return "create-task";
    }

    /**
     * Обработка создания новой задачи.
     */
    @PostMapping("/new")
    public String createTask(TaskForm form, Model model) {
        User forester = userService.getUserById(form.getForesterId());
        logger.info("Создание задачи для лесника {} и растения {}", forester.getUsername(), form.getPlantName());

        if (!taskService.isValidPlant(form.getPlantName())) {
            model.addAttribute("error", "Неверное растение");
            model.addAttribute("task", form);
            model.addAttribute("foresters", userService.getAllForesters());
            return "create-task";
        }

        taskService.createTaskFromForm(form);
        model.addAttribute("success", "Задача успешно создана!");

        return "redirect:/tasks/owner-tasks";
    }

    /**
     * Отображение задач для текущего лесника.
     */
    @GetMapping("/forester-tasks")
    public String showTasksForForester(Model model, Principal principal) {
        logger.info("Показ задач для лесника {}", principal.getName());

        User forester = userService.findByUsername(principal.getName());
        if (forester == null) {
            throw new RuntimeException("Лесник не найден");
        }

        List<Task> tasks = taskService.getTasksForForester(forester);
        model.addAttribute("tasks", tasks);

        return "forester-tasks";
    }

    /**
     * Старт задачи лесником (изменение статуса на IN_PROGRESS).
     */
    @PostMapping("/start/{id}")
    public String startTask(@PathVariable Long id) {
        logger.info("Лесник начал задачу ID {}", id);

        Task task = taskService.getById(id);
        task.setStatus(Status.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);

        return "redirect:/tasks/forester-tasks";
    }

    /**
     * Завершение задачи лесником с заполнением отчета.
     */
    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id,
                               @RequestParam String reportText,
                               Principal principal,
                               Model model) {

        logger.info("Завершение задачи ID {} лесником {}", id, principal.getName());

        // Проверка наличия отчета
        if (reportText == null || reportText.trim().isEmpty()) {
            User forester = userService.findByUsername(principal.getName());
            if (forester == null) {
                throw new RuntimeException("Лесник не найден");
            }

            List<Task> tasks = taskService.getTasksForForester(forester);
            model.addAttribute("tasks", tasks);
            model.addAttribute("error", "Пожалуйста, заполните отчёт перед завершением задачи.");

            return "forester-tasks";
        }

        // Используем стратегию выполнения задачи
        taskService.completeTaskWithStrategyAndNotification(id, reportText);

        return "redirect:/tasks/forester-tasks";
    }

    /**
     * Отображение карты с маршрутом задачи.
     */
    @GetMapping("/{id}/map")
    public String showTaskMap(@PathVariable Long id,
                              @RequestParam(required = false) String from,
                              Model model,
                              Principal principal) {

        logger.info("Показ карты для задачи ID {} пользователем {}", id, principal.getName());

        Task task = taskService.getById(id);
        User currentUser = userService.findByUsername(principal.getName());

        if (currentUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        model.addAttribute("task", task);
        model.addAttribute("userRole", currentUser.getRole().name());
        model.addAttribute("from", from); // для возврата на страницу, откуда пришли

        return "map";
    }

    /**
     * Просмотр истории завершённых задач.
     */
    @GetMapping("/history")
    public String showTaskHistory(Model model) {
        logger.info("Просмотр истории задач");

        List<Task> historyTasks = taskService.getAllTasks().stream()
                .filter(Task::isApprovedByOwner)
                .collect(Collectors.toList());

        model.addAttribute("tasks", historyTasks);
        return "history";
    }

    /**
     * Удаление задачи по ID.
     */
    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        logger.info("Удаление задачи ID {}", id);
        taskService.deleteById(id);
        return "redirect:/tasks/history";
    }
}
