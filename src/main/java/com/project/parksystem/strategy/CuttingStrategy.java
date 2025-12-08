package com.project.parksystem.strategy;

import com.project.parksystem.model.Task;
import com.project.parksystem.model.Tree;
import com.project.parksystem.service.TreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Стратегия выполнения задачи по вырубке растений.
 */
@Component
public class CuttingStrategy implements TaskStrategy {

    private final TreeService treeService;
    private static final Logger log = LoggerFactory.getLogger(CuttingStrategy.class);

    public CuttingStrategy(TreeService treeService) {
        this.treeService = treeService;
    }

    @Override
    public void process(Task task) {

        log.info("🪓 Выполнение вырубки...");

        // найти дерево в радиусе точки
        List<Tree> trees = treeService.findTreesInRadius(
                task.getCoordX(), task.getCoordY()
        );

        if (trees.isEmpty()) {
            log.warn("Нет дерева для вырубки.");
            return;
        }

        Tree target = trees.get(0);

        // удалить
        treeService.cutTree(target);

        log.info("Дерево {} вырублено!", target.getId());
    }

    @Override
    public String getSupportedAction() {
        return "CUTTING";
    }
}

