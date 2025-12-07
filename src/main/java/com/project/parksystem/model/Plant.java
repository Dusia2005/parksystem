package com.project.parksystem.model;

import lombok.Data;

/**
 * Модель растения.
 */
@Data
public class Plant {
    private Long id;
    private String name;
    private String imageFilename; // новое поле
}
