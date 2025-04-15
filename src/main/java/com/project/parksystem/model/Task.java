package com.project.parksystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private User owner;

    @ManyToOne
    private User forester;

    @ManyToOne
    private Plant plant; // Связываем задачу с растением

    private boolean approvedByOwner; // Подтверждение выполнения

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
