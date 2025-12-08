package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import com.project.parksystem.model.Tree;
import com.project.parksystem.repository.TreeJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия тушения пожаров.
 */
@Component
public class FireExtinguishingStrategy implements TaskStrategy {

    private final TreeJdbcRepository repo;
    private static final Logger log = LoggerFactory.getLogger(FireExtinguishingStrategy.class);

    public FireExtinguishingStrategy(TreeJdbcRepository repo) {
        this.repo = repo;
    }

    @Override
    public void process(Task task) {

        log.info("🔥 Тушение пожара...");

        List<Tree> trees = repo.findAllInRadius(task.getCoordX(), task.getCoordY(), 60);
        if (trees.isEmpty()) {
            log.warn("Нет дерева в зоне пожара.");
            return;
        }

        Tree t = trees.get(0);

        long hours = Duration.between(task.getCreatedAt(), LocalDateTime.now()).toHours();

        if (hours > 2) {
            log.warn("Дерево сгорело!");
            repo.delete(t.getId());
            return;
        }

        t.setStatus("SAVED");
        t.setLastActionAt(LocalDateTime.now());
        repo.update(t);

        log.info("Пожар потушен, дерево спасено!");
    }

    @Override
    public String getSupportedAction() {
        return "FIRE_EXTINGUISHING";
    }
}

