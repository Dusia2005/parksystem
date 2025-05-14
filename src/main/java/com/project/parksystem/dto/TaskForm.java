package com.project.parksystem.dto;

import com.project.parksystem.model.Action;
import lombok.Data;

/**
 * DTO-форма для создания задачи.
 */
@Data
public class TaskForm {
    private String description;
    private Long foresterId;
    private String plantName;
    private Action action;
    private Integer coordX;
    private Integer coordY;
}
