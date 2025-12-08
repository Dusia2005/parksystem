package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import com.project.parksystem.model.Tree;
import com.project.parksystem.repository.TreeJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Стратегия полива растений.
 */
@Component
public class WateringStrategy implements TaskStrategy {

    private final TreeJdbcRepository repo;
    private static final Logger log = LoggerFactory.getLogger(WateringStrategy.class);

    public WateringStrategy(TreeJdbcRepository repo) {
        this.repo = repo;
    }

    @Override
    public void process(Task task) {

        log.info("💧 Полив...");

        List<Tree> trees = repo.findAllInRadius(task.getCoordX(), task.getCoordY(), 60);
        if (trees.isEmpty()) {
            log.warn("Нет дерева для полива.");
            return;
        }

        Tree t = trees.get(0);
        t.setStatus("WATERED");
        t.setLastActionAt(LocalDateTime.now());

        repo.update(t);
        log.info("Дерево {} полито!", t.getId());
    }

    @Override
    public String getSupportedAction() {
        return "WATERING";
    }
}

