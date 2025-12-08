package com.project.parksystem.service;

import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Tree;
import com.project.parksystem.repository.TreeJdbcRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TreeService {

    private final TreeJdbcRepository treeRepo;

    public static final int TREE_RADIUS = 10;
    private final int HOME_X = 321; // центр карты (подгоняем под твою картинку)
    private final int HOME_Y = 128;
    private final int HOME_RADIUS = 10;

    public boolean isInsideHome(int x, int y) {
        double dist = Math.sqrt(Math.pow(x - HOME_X, 2) + Math.pow(y - HOME_Y, 2));
        return dist < HOME_RADIUS;
    }
    
    public TreeService(TreeJdbcRepository treeRepo) {
        this.treeRepo = treeRepo;
    }

    public List<Tree> getAllTrees() {
        List<Tree> trees = treeRepo.findAll();
        return trees;
    }


    public Tree plantTree(Plant plant, int x, int y) {
        Tree tree = new Tree();
        tree.setPlant(plant);
        tree.setCoordX(x);
        tree.setCoordY(y);
        tree.setCreatedAt(LocalDateTime.now());

        treeRepo.saveAndReturn(tree);
        return tree;
    }

    public void cutTree(Tree tree) {
        treeRepo.delete(tree.getId());
    }

    public List<Tree> findTreesInRadius(int x, int y) {
        return treeRepo.findAllInRadius(x, y, TREE_RADIUS);
    }

    public boolean isPlaceOccupied(int x, int y, int radius) {
        List<Tree> trees = treeRepo.findAll();
        for (Tree t : trees) {
            double dist = Math.sqrt(
                    Math.pow(x - t.getCoordX(), 2) + Math.pow(y - t.getCoordY(), 2)
            );
            if (dist < radius) return true;
        }
        return false;
    }

    public Plant findTreeAt(int x, int y, int radius) {
        List<Tree> trees = treeRepo.findAll();
        for (Tree t : trees) {
            double dist = Math.sqrt(
                    Math.pow(x - t.getCoordX(), 2) + Math.pow(y - t.getCoordY(), 2)
            );
            if (dist < radius) return t.getPlant();
        }
        return null;
    }




}
