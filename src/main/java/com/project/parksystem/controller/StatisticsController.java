package com.project.parksystem.controller;

import com.project.parksystem.dto.ForesterStatisticsDto;
import com.project.parksystem.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public String getStatistics(
            @RequestParam(value = "sort", required = false, defaultValue = "id") String sort,
            Model model) {
        logger.info("Получение статистики, сортировка по {}", sort);
        List<ForesterStatisticsDto> stats = statisticsService.getForestersStatistics();

        switch (sort) {
            case "kilometers" -> stats.sort(Comparator.comparingDouble(ForesterStatisticsDto::getKilometers).reversed());
            case "completed" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getCompletedTasks).reversed());
            case "inProgress" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getInProgressTasks).reversed());
            case "pending" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getPendingTasks).reversed());
            case "workTime" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getTotalWorkTime).reversed());
            case "level" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getLevel).reversed());
            case "username" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getUsername));
            default -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getId)); // по умолчанию по ID
        }

        model.addAttribute("stats", stats);
        model.addAttribute("currentSort", sort); // можно использовать для подсветки в UI
        return "statistics";
    }
}
