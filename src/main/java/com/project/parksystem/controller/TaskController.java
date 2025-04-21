package com.project.parksystem.controller;

import com.project.parksystem.dto.TaskForm;
import com.project.parksystem.model.Report;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import com.project.parksystem.service.ReportService;
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
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        HashMap<Long, String> taskReports = new HashMap<>();

        for (Task task : tasks) {
            reportService.getReportByTask(task).ifPresent(report ->
                    taskReports.put(task.getId(), report.getReportText())
            );
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("taskReports", taskReports); // Добавим отчёты к задачам
        return "tasks";
    }

    @PostMapping("/approve/{taskId}")
    public String approveTask(@PathVariable Long taskId) {
        taskService.approveTask(taskId);
        return "redirect:/tasks";
    }

    @GetMapping("/new")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("task", new TaskForm());
        model.addAttribute("foresters", userService.getAllForesters());

        List<String> allowedPlants = List.of(
                "ель", "сосна", "береза", "дуб", "липа", "елка",
                "роза", "тюльпаны", "ландыши", "лилия"
        );
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
        return "redirect:/tasks";
    }

    @GetMapping("/forester/tasks")
    public String showTasksForForester(Model model, Principal principal) {
        User forester = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Лесник не найден"));
        List<Task> tasks = taskService.getTasksForForester(forester);
        model.addAttribute("tasks", tasks);
        return "forester/tasks";
    }

    @PostMapping("/start/{id}")
    public String startTask(@PathVariable Long id) {
        Task task = taskService.getById(id);
        task.setStatus(Status.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);
        return "redirect:/tasks/forester/tasks";
    }


    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id, @RequestParam String reportText, Principal principal) {
        Task task = taskService.getById(id);
        task.setStatus(Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());
        taskService.save(task);

        User forester = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Лесник не найден"));
        List<Task> tasks = taskService.getTasksForForester(forester);

        Report report = new Report();
        report.setTask(task);
        report.setForester(forester);
        report.setReportText(reportText);
        report.setCreatedAt(LocalDateTime.now());
        reportService.save(report);

        return "redirect:/tasks/forester/tasks";
    }
}
