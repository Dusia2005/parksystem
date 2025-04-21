package com.project.parksystem.service;

import com.project.parksystem.model.Report;
import com.project.parksystem.model.Task;
import com.project.parksystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public void saveReport(Report report) {
        reportRepository.save(report);
    }

    public void save(Report report) {
        reportRepository.save(report);
    }
    public Optional<Report> getReportByTask(Task task) {
        return reportRepository.findByTask(task);
    }
}
