package com.project.parksystem.repository;

import com.project.parksystem.model.Task;
import com.project.parksystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByForester(User forester); // Метод для поиска задач лесника
}

