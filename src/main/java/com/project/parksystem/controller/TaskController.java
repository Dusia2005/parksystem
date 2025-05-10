package com.project.parksystem.controller;

import com.project.parksystem.dto.ForesterStatisticsDto;
import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import com.project.parksystem.service.PlantService;
import com.project.parksystem.service.StatisticsService;
import com.project.parksystem.service.TaskService;
import com.project.parksystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final StatisticsService statisticsService;

    public TaskController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/owner-tasks")
    public String listTasks(Model model) {
        logger.info("Загрузка задач для владельца");
        List<Task> tasks = taskService.getAllTasks().stream()
                .filter(task -> !task.isApprovedByOwner())
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        return "owner-tasks";
    }

    @PostMapping("/approve/{taskId}")
    public String approveTask(@PathVariable Long taskId) {
        logger.info("Одобрение задачи ID {}", taskId);
        Task task = taskService.getById(taskId); // сначала получаем задачу
        task.setApprovedByOwner(true); // ставим флажок
        task.setCompletedByOwnerAt(LocalDateTime.now()); // ставим дату завершения
        task.setUpdatedAt(LocalDateTime.now()); // обновляем дату обновления (опционально)
        taskService.save(task); // сохраняем изменения

        return "redirect:/tasks/owner-tasks"; // редиректим обратно
    }

    @GetMapping("/new")
    public String showCreateTaskForm(Model model) {
        logger.info("Открытие формы создания задачи");
        model.addAttribute("task", new TaskForm());
        model.addAttribute("foresters", userService.getAllForesters());

        List<Plant> allowedPlants = plantService.getAllPlants();
        model.addAttribute("allowedPlants", allowedPlants);

        return "create-task";
    }


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

    @GetMapping("/forester-tasks")
    public String showTasksForForester(Model model, Principal principal) {
        logger.info("Показ задач для лесника {}", principal.getName());
        User forester = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Лесник не найден"));
        List<Task> tasks = taskService.getTasksForForester(forester);
        model.addAttribute("tasks", tasks);
        return "forester-tasks";
    }

    @PostMapping("/start/{id}")
    public String startTask(@PathVariable Long id) {
        logger.info("Лесник начал задачу ID {}", id);
        Task task = taskService.getById(id);
        task.setStatus(Status.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);
        return "redirect:/tasks/forester-tasks";
    }


    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id,
                               @RequestParam String reportText,
                               Principal principal,
                               Model model) {
        logger.info("Завершение задачи ID {} лесником {}", id, principal.getName());
        if (reportText == null || reportText.trim().isEmpty()) {
            Task task = taskService.getById(id);
            User forester = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Лесник не найден"));
            List<Task> tasks = taskService.getTasksForForester(forester);

            model.addAttribute("tasks", tasks);
            model.addAttribute("error", "Пожалуйста, заполните отчёт перед завершением задачи.");
            return "forester-tasks";
        }

        Task task = taskService.getById(id);
        task.setReportText(reportText); // 💥 добавлено!
        task.setStatus(Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());

        taskService.save(task);

        return "redirect:/tasks/forester-tasks";
    }

    @GetMapping("/{id}/map")
    public String showTaskMap(@PathVariable Long id,
                              @RequestParam(required = false) String from,
                              Model model,
                              Principal principal) {
        logger.info("Показ карты для задачи ID {} пользователем {}", id, principal.getName());
        Task task = taskService.findById(id);
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("task", task);
        model.addAttribute("userRole", currentUser.getRole().name());
        model.addAttribute("from", from); // 👈 теперь param.from будет работать

        return "map"; // убедись, что шаблон называется map.html
    }


    @GetMapping("/history")
    public String showTaskHistory(Model model) {
        logger.info("Просмотр истории задач");
        List<Task> historyTasks = taskService.getAllTasks().stream()
                .filter(Task::isApprovedByOwner)
                .collect(Collectors.toList());


        model.addAttribute("tasks", historyTasks);
        return "history";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        logger.info("Удаление задачи ID {}", id);
        taskService.deleteById(id);
        return "redirect:/tasks/history";
    }
}
