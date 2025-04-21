package com.project.parksystem.controller;

import com.project.parksystem.model.Report;
import com.project.parksystem.model.User;
import com.project.parksystem.service.ReportService;
import com.project.parksystem.service.TaskService;
import com.project.parksystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "reports"; // templates/reports.html
    }

    @GetMapping("/new")
    public String newReportForm(Model model) {
        model.addAttribute("report", new Report());
        model.addAttribute("tasks", taskService.getAllTasks());
        return "new-report"; // Форма создания отчета (templates/create-task.html)
    }

    @PostMapping("/save")
    public String saveReport(@ModelAttribute Report report, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> foresterOpt = userService.findByUsername(username);

        if (foresterOpt.isPresent()) {
            User forester = foresterOpt.get();
            report.setForester(forester);
            report.setCreatedAt(LocalDateTime.now());
            reportService.saveReport(report);
        } else {
            throw new RuntimeException("User not found: " + username);
        }

        return "redirect:/reports";
    }
}
