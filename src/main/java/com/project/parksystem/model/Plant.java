package com.project.parksystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private String condition; // Например: "Здоровое", "Больное", "Нуждается в обработке"
}
