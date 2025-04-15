package com.project.parksystem.service;

import com.project.parksystem.model.Task;
import com.project.parksystem.model.Status;
import com.project.parksystem.model.User;
import com.project.parksystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksForForester(User forester) {
        return taskRepository.findByForester(forester);
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }


    public void approveTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(Status.COMPLETED); // Или другой статус, если есть статус "APPROVED"
        taskRepository.save(task);
    }
}
