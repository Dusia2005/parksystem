package com.project.parksystem.controller;

import com.project.parksystem.model.Plant;
import com.project.parksystem.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/plants")
public class PlantController {
    private static final Logger logger = LoggerFactory.getLogger(PlantController.class);

    @Autowired
    private PlantService plantService;

    @GetMapping
    public String listPlants(Model model) {
        logger.info("Запрошен список растений");
        model.addAttribute("plants", plantService.findAll());
        return "plants";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        logger.info("Открыта форма добавления нового растения");
        model.addAttribute("plant", new Plant());
        return "plant-form";
    }

    @PostMapping("/new")
    public String createPlant(@ModelAttribute Plant plant, Model model) {
        logger.info("Попытка создать новое растение: {}", plant.getName());

        if (plantService.existsByNameIgnoreCase(plant.getName())) {
            logger.warn("Растение {} уже существует", plant.getName());
            model.addAttribute("error", "Растение с таким названием уже существует!");
            return "plant-form";
        }
        plantService.savePlant(plant);
        logger.info("Растение {} успешно создано", plant.getName());
        return "redirect:/plants";
    }

    @GetMapping("/edit/{id}")
    public String editPlant(@PathVariable Long id, Model model) {
        logger.info("Открытие формы редактирования растения ID {}", id);
        Plant plant = plantService.findById(id);
        model.addAttribute("plant", plant);
        return "plant-form";
    }

    @PostMapping("/update/{id}")
    public String updatePlant(@PathVariable Long id, @ModelAttribute Plant plant, Model model) {
        logger.info("Обновление растения ID {} -> {}", id, plant.getName());
        Plant existing = plantService.findById(id);

        if (!existing.getName().equalsIgnoreCase(plant.getName()) &&
                plantService.existsByNameIgnoreCase(plant.getName())) {
            logger.warn("Растение {} уже существует при обновлении", plant.getName());
            model.addAttribute("error", "Растение с таким названием уже существует!");
            model.addAttribute("plant", plant);
            return "plant-form";
        }

        existing.setName(plant.getName());
        plantService.savePlant(existing);
        return "redirect:/plants";
    }

    @PostMapping("/delete/{id}")
    public String deletePlant(@PathVariable Long id) {
        logger.info("Удаление растения ID {}", id);
        plantService.deleteById(id);
        return "redirect:/plants";
    }
}

