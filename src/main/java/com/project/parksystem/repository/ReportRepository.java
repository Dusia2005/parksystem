package com.project.parksystem.repository;

import com.project.parksystem.model.Report;
import com.project.parksystem.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByTask(Task task);
}
