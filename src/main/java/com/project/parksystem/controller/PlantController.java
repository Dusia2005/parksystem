package com.project.parksystem.controller;

import com.project.parksystem.model.Plant;
import com.project.parksystem.service.PlantFileService;
import com.project.parksystem.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

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
    private final PlantFileService fileService;

    public PlantController(PlantService plantService, PlantFileService fileService) {
        this.plantService = plantService;
        this.fileService = fileService;
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
    public String createPlant(@ModelAttribute Plant plant,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam(value = "useDefault", required = false) Boolean useDefault,
                              Model model) {

        if (plantService.existsByNameIgnoreCase(plant.getName())) {
            model.addAttribute("error", "Растение с таким названием уже существует!");
            return "plant-form";
        }

        try {
            String storedFilename = null;

            // 1️⃣ Если загрузили файл — сохраняем его
            if (imageFile != null && !imageFile.isEmpty()) {
                storedFilename = fileService.store(imageFile);
            }
            // 2️⃣ Если не загрузили файл и выбрали "использовать дефолт"
            else if (useDefault != null && useDefault) {
                storedFilename = fileService.copyDefaultImage();
            }
            // 3️⃣ Иначе ошибка — файл обязателен
            else {
                model.addAttribute("error", "Вы не выбрали изображение. Хотите использовать картинку по умолчанию?");
                model.addAttribute("needDefaultChoice", true);
                model.addAttribute("plant", plant);
                return "plant-form";
            }

            plant.setImageFilename(storedFilename);
            plantService.savePlant(plant);
            return "redirect:/plants";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("plant", plant);
            return PLANT_FORM_VIEW;
        }
    }


    @GetMapping("/edit/{id}")
    public String editPlant(@PathVariable Long id, Model model) {
        LOGGER.info("Открытие формы редактирования растения ID {}", id);
        model.addAttribute(PLANT_ATTR, plantService.findById(id));
        return PLANT_FORM_VIEW;
    }

    @PostMapping("/update/{id}")
    public String updatePlant(@PathVariable Long id,
                              @ModelAttribute Plant plant,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Model model) {
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

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // если был предыдущий файл — удаляем
                if (!StringUtils.isEmpty(existing.getImageFilename())) {
                    fileService.delete(existing.getImageFilename());
                }
                String filename = fileService.store(imageFile);
                existing.setImageFilename(filename);
            }
            plantService.savePlant(existing); // savePlant делает insert — убедись, что для update используется update(...) или savePlant вызывает update при наличии id
            // Если у тебя savePlant всегда вставляет — вызови plantJdbcRepository.update(existing) здесь вместо savePlant
        } catch (Exception ex) {
            model.addAttribute(ERROR_ATTR, ex.getMessage());
            model.addAttribute(PLANT_ATTR, plant);
            return PLANT_FORM_VIEW;
        }

        return "redirect:/plants";
    }

    @PostMapping("/delete/{id}")
    public String deletePlant(@PathVariable Long id) {
        LOGGER.info("Удаление растения ID {}", id);
        plantService.deleteById(id);
        return "redirect:/plants";
    }

    // Отдача изображения по имени
    @GetMapping("/image/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws MalformedURLException {
        Resource file = fileService.loadAsResource(filename);
        if (file == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(file);
    }
}
