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

@Controller
@RequestMapping("/tasks")
public class TaskController {

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
        List<Task> tasks = taskService.getAllTasks().stream()
                .filter(task -> !task.isApprovedByOwner())
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        return "owner-tasks";
    }

    @PostMapping("/approve/{taskId}")
    public String approveTask(@PathVariable Long taskId) {
        Task task = taskService.getById(taskId); // сначала получаем задачу
        task.setApprovedByOwner(true); // ставим флажок
        task.setCompletedByOwnerAt(LocalDateTime.now()); // ставим дату завершения
        task.setUpdatedAt(LocalDateTime.now()); // обновляем дату обновления (опционально)
        taskService.save(task); // сохраняем изменения

        return "redirect:/tasks/owner-tasks"; // редиректим обратно
    }

    @GetMapping("/new")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new TaskForm());
        model.addAttribute("foresters", userService.getAllForesters());

        List<Plant> allowedPlants = plantService.getAllPlants();
        model.addAttribute("allowedPlants", allowedPlants);

        return "create-task";
    }


    @PostMapping("/new")
    public String createTask(TaskForm form, Model model) {
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
        User forester = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Лесник не найден"));
        List<Task> tasks = taskService.getTasksForForester(forester);
        model.addAttribute("tasks", tasks);
        return "forester-tasks";
    }

    @PostMapping("/start/{id}")
    public String startTask(@PathVariable Long id) {
        Task task = taskService.getById(id);
        task.setStatus(Status.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);
        return "redirect:/tasks/forester-tasks";
    }


    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, @RequestParam String reportText, Principal principal, Model model) {
        if (reportText == null || reportText.trim().isEmpty()) {
            // Получаем задачу и список задач снова, чтобы вернуть всё на страницу
            Task task = taskService.getById(id);
            User forester = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Лесник не найден"));
            List<Task> tasks = taskService.getTasksForForester(forester);

            model.addAttribute("tasks", tasks);
            model.addAttribute("error", "Пожалуйста, заполните отчёт перед завершением задачи.");
            return "forester-tasks";
        }

        Task task = taskService.getById(id);
        task.setStatus(Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);

        User forester = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Лесник не найден"));

        taskService.save(task);

        return "redirect:/tasks/forester-tasks";
    }

    @GetMapping("/{id}/map")
    public String showTaskMap(@PathVariable Long id, Model model, Principal principal) {
        Task task = taskService.findById(id); // Убедись, что у тебя есть такой метод
        User currentUser = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("task", task);
        model.addAttribute("userRole", currentUser.getRole().name()); // например: "FORESTER" или "OWNER"

        return "map";
    }

    @GetMapping("/history")
    public String showTaskHistory(Model model) {
        List<Task> historyTasks = taskService.getAllTasks().stream()
                .filter(Task::isApprovedByOwner)
                .collect(Collectors.toList());


        model.addAttribute("tasks", historyTasks);
        return "history";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/tasks/history";
    }
}
