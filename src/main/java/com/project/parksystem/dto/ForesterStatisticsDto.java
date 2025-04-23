package com.project.parksystem.dto;

import java.time.Duration;

public class ForesterStatisticsDto {
    private Long id;
    private String username;
    private double kilometers;
    private long completedTasks;
    private long inProgressTasks;
    private long pendingTasks;
    private Duration totalWorkTime;
    private String level; // "Новичок", "Опытный", "Знаток леса"

    // геттеры и сеттеры

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public void setTotalWorkTime(Duration totalWorkTime) {
        this.totalWorkTime = totalWorkTime;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public double getKilometers() {
        return kilometers;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public Duration getTotalWorkTime() {
        return totalWorkTime;
    }

    public String getLevel() {
        return level;
    }

    public String getFormattedWorkTime() {
        long totalMinutes = totalWorkTime.toMinutes();
        long days = totalMinutes / (60 * 24);
        long hours = (totalMinutes % (60 * 24)) / 60;
        long minutes = totalMinutes % 60;

        return String.format("%d д. %d ч. %d мин.", days, hours, minutes);
    }

}
