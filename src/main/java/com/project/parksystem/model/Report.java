package com.project.parksystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User forester;

    private String reportText;
    private LocalDateTime createdAt;

    // Сеттер для forester
    public void setForester(User forester) {
        this.forester = forester;
    }

    // Сеттер для createdAt
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
