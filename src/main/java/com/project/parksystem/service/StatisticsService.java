package com.project.parksystem.service;

import com.project.parksystem.dto.ForesterStatisticsDto;
import com.project.parksystem.model.Role;
import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import com.project.parksystem.repository.TaskRepository;
import com.project.parksystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public StatisticsService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public double getRoundTripDistanceInKm(int x, int y) {
        final int baseX = 320;
        final int baseY = 122;
        final double metersPerPixel = 100.0 / 92.0;

        double dx = x - baseX;
        double dy = y - baseY;
        double pixelDistance = Math.sqrt(dx * dx + dy * dy);
        double distanceMeters = pixelDistance * metersPerPixel;
        return (distanceMeters * 2) / 1000.0;
    }

    public List<ForesterStatisticsDto> getForestersStatistics() {
        List<User> foresters = userRepository.findAllByRole(Role.valueOf("FORESTER"));
        List<ForesterStatisticsDto> stats = new ArrayList<>();

        for (User forester : foresters) {
            List<Task> tasks = taskRepository.findByForester(forester);

            double totalKm = 0;
            long completed = 0;
            long inProgress = 0;
            long pending = 0;
            Duration totalDuration = Duration.ZERO;

            for (Task task : tasks) {
                switch (task.getStatus()) {
                    case COMPLETED -> {
                        completed++;

                        // Считаем километры ТОЛЬКО для завершённых задач
                        if (task.getCoordX() != null && task.getCoordY() != null) {
                            totalKm += getRoundTripDistanceInKm(task.getCoordX(), task.getCoordY());
                        }

                        if (task.getCreatedAt() != null && task.getUpdatedAt() != null) {
                            totalDuration = totalDuration.plus(Duration.between(task.getCreatedAt(), task.getUpdatedAt()));
                        }
                    }
                    case IN_PROGRESS -> inProgress++;
                    case PENDING -> pending++;
                }
            }

            String level = calculateLevel(completed, totalKm, totalDuration);

            ForesterStatisticsDto dto = new ForesterStatisticsDto();
            dto.setId(forester.getId());
            dto.setUsername(forester.getUsername());
            dto.setKilometers(totalKm);
            dto.setCompletedTasks(completed);
            dto.setInProgressTasks(inProgress);
            dto.setPendingTasks(pending);
            dto.setTotalWorkTime(totalDuration);
            dto.setLevel(level);

            stats.add(dto);
        }

        return stats;
    }

    private String calculateLevel(long completed, double km, Duration time) {
        if (completed >= 20 && km >= 50 && time.toHours() >= 40) return "Знаток леса";
        if (completed >= 10 && km >= 20 && time.toHours() >= 10) return "Опытный";
        return "Новичок";
    }
}

