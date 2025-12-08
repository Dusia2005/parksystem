package com.project.parksystem.controller;

import com.project.parksystem.model.Plant;
import com.project.parksystem.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plants")
public class PlantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public String listPlants(Model model) {
        model.addAttribute("plants", plantService.findAll());
        return "plants";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("plant", new Plant());
        model.addAttribute("images", plantService.getAvailableImages());
        return "plant-form";
    }

    @PostMapping("/new")
    public String createPlant(@ModelAttribute Plant plant, Model model) {

        if (plantService.existsByNameIgnoreCase(plant.getName())) {
            model.addAttribute("error", "Растение с таким названием уже существует!");
            model.addAttribute("images", plantService.getAvailableImages());
            return "plant-form";
        }

        if (plant.getImageFilename() == null || plant.getImageFilename().isBlank()) {
            model.addAttribute("error", "Выберите изображение!");
            model.addAttribute("images", plantService.getAvailableImages());
            return "plant-form";
        }

        plantService.savePlant(plant);
        return "redirect:/plants";
    }


    @GetMapping("/edit/{id}")
    public String editPlant(@PathVariable Long id, Model model) {
        model.addAttribute("plant", plantService.findById(id));
        model.addAttribute("images", plantService.getAvailableImages());
        return "plant-form";
    }

    @PostMapping("/update/{id}")
    public String updatePlant(@PathVariable Long id,
                              @ModelAttribute Plant plant,
                              Model model) {

        Plant existing = plantService.findById(id);

        if (!existing.getName().equalsIgnoreCase(plant.getName())
                && plantService.existsByNameIgnoreCase(plant.getName())) {

            model.addAttribute("error", "Растение с таким названием уже существует!");
            model.addAttribute("images", plantService.getAvailableImages());
            return "plant-form";
        }

        existing.setName(plant.getName());
        existing.setImageFilename(plant.getImageFilename());

        plantService.updatePlant(existing);

        return "redirect:/plants";
    }

    @PostMapping("/delete/{id}")
    public String deletePlant(@PathVariable Long id) {
        plantService.deleteById(id);
        return "redirect:/plants";
    }
}
