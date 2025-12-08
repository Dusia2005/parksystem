package com.project.parksystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tree {
    private Long id;
    private Plant plant;
    private Integer coordX;
    private Integer coordY;
    private LocalDateTime createdAt;

    private String status;
    private LocalDateTime lastActionAt;
    private boolean deleted;
}


