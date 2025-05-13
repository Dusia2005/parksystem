package com.project.parksystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private String description;
    private User forester;
    private Action action;
    private Plant plant;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean approvedByOwner;
    private LocalDateTime completedByOwnerAt;
    private String reportText;
    private Integer coordX;
    private Integer coordY;
}
