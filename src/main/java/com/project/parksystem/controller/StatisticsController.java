package com.project.parksystem.controller;

import com.project.parksystem.dto.ForesterStatisticsDto;
import com.project.parksystem.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Comparator;
import java.util.List;

/**
 * Контроллер для отображения статистики работы лесников.
 */
@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);
    private static final String STATS_ATTR = "stats";
    private static final String CURRENT_SORT_ATTR = "currentSort";
    private static final String DEFAULT_SORT = "id";
    private static final String STATISTICS_VIEW = "statistics";

    private final StatisticsService statisticsService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param statisticsService сервис для работы со статистикой
     */
    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public String getStatistics(
            @RequestParam(value = "sort", required = false, defaultValue = DEFAULT_SORT) String sort,
            Model model) {

        LOGGER.info("Получение статистики, сортировка по {}", sort);
        List<ForesterStatisticsDto> stats = statisticsService.getForestersStatistics();
        sortStatistics(stats, sort);

        model.addAttribute(STATS_ATTR, stats);
        model.addAttribute(CURRENT_SORT_ATTR, sort);
        return STATISTICS_VIEW;
    }

    /**
     * Сортирует статистику по заданному параметру.
     */
    private void sortStatistics(List<ForesterStatisticsDto> stats, String sort) {
        switch (sort) {
            case "kilometers" -> stats.sort(Comparator.comparingDouble(ForesterStatisticsDto::getKilometers).reversed());
            case "completed" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getCompletedTasks).reversed());
            case "inProgress" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getInProgressTasks).reversed());
            case "pending" -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getPendingTasks).reversed());
            case "workTime" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getTotalWorkTime).reversed());
            case "level" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getLevel).reversed());
            case "username" -> stats.sort(Comparator.comparing(ForesterStatisticsDto::getUsername));
            default -> stats.sort(Comparator.comparingLong(ForesterStatisticsDto::getId));
        }
    }
}