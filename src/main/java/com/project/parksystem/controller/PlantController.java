package com.project.parksystem.controller;

import com.project.parksystem.model.Plant;
import com.project.parksystem.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для обработки запросов, связанных с растениями.
 */
@Controller
@RequestMapping("/plants")
public class PlantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlantController.class);
    private static final String PLANTS_ATTR = "plants";
    private static final String PLANT_ATTR = "plant";
    private static final String ERROR_ATTR = "error";
    private static final String PLANT_FORM_VIEW = "plant-form";
    private static final String PLANTS_VIEW = "plants";

    private final PlantService plantService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param plantService сервис для работы с растениями
     */
    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public String listPlants(Model model) {
        LOGGER.info("Запрошен список растений");
        model.addAttribute(PLANTS_ATTR, plantService.findAll());
        return PLANTS_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        LOGGER.info("Открыта форма добавления нового растения");
        model.addAttribute(PLANT_ATTR, new Plant());
        return PLANT_FORM_VIEW;
    }

    @PostMapping("/new")
    public String createPlant(@ModelAttribute Plant plant, Model model) {
        LOGGER.info("Попытка создать новое растение: {}", plant.getName());

        if (plantService.existsByNameIgnoreCase(plant.getName())) {
            LOGGER.warn("Растение {} уже существует", plant.getName());
            model.addAttribute(ERROR_ATTR, "Растение с таким названием уже существует!");
            return PLANT_FORM_VIEW;
        }

        plantService.savePlant(plant);
        LOGGER.info("Растение {} успешно создано", plant.getName());
        return "redirect:/plants";
    }

    @GetMapping("/edit/{id}")
    public String editPlant(@PathVariable Long id, Model model) {
        LOGGER.info("Открытие формы редактирования растения ID {}", id);
        model.addAttribute(PLANT_ATTR, plantService.findById(id));
        return PLANT_FORM_VIEW;
    }

    @PostMapping("/update/{id}")
    public String updatePlant(@PathVariable Long id, @ModelAttribute Plant plant, Model model) {
        LOGGER.info("Обновление растения ID {} -> {}", id, plant.getName());
        Plant existing = plantService.findById(id);

        if (!existing.getName().equalsIgnoreCase(plant.getName())
                && plantService.existsByNameIgnoreCase(plant.getName())) {
            LOGGER.warn("Растение {} уже существует при обновлении", plant.getName());
            model.addAttribute(ERROR_ATTR, "Растение с таким названием уже существует!");
            model.addAttribute(PLANT_ATTR, plant);
            return PLANT_FORM_VIEW;
        }

        existing.setName(plant.getName());
        plantService.savePlant(existing);
        return "redirect:/plants";
    }

    @PostMapping("/delete/{id}")
    public String deletePlant(@PathVariable Long id) {
        LOGGER.info("Удаление растения ID {}", id);
        plantService.deleteById(id);
        return "redirect:/plants";
    }
}