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

    @ManyToOne
    @JoinColumn(name = "forester_id")
    private User forester;

    @Enumerated(EnumType.STRING)
    private Action action;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "approved_by_owner", nullable = false)
    private boolean approvedByOwner = false;

    @Column(name = "completed_by_owner_at")
    private LocalDateTime completedByOwnerAt;

    private String reportText;

    // Добавь, если используешь: координаты
    private Integer coordX;
    private Integer coordY;

    // Геттеры/сеттеры:
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setForester(User forester) {
        this.forester = forester;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public void setCoordX(Integer coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(Integer coordY) {
        this.coordY = coordY;
    }

    public void setApprovedByOwner(boolean approvedByOwner) {
        this.approvedByOwner = approvedByOwner;
    }

    public LocalDateTime getCompletedByOwnerAt() { return completedByOwnerAt; }

    public void setCompletedByOwnerAt(LocalDateTime completedByOwnerAt) { this.completedByOwnerAt = completedByOwnerAt;}
}