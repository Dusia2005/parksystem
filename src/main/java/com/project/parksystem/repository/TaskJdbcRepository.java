package com.project.parksystem.repository;

import com.project.parksystem.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskJdbcRepository {

    private final JdbcTemplate jdbc;

    public TaskJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setDescription(rs.getString("description"));
        task.setAction(Action.valueOf(rs.getString("action")));
        task.setStatus(Status.valueOf(rs.getString("status")));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        task.setApprovedByOwner(rs.getBoolean("approved_by_owner"));
        Timestamp completedTs = rs.getTimestamp("completed_by_owner_at");
        if (completedTs != null) task.setCompletedByOwnerAt(completedTs.toLocalDateTime());
        task.setReportText(rs.getString("report_text"));
        task.setCoordX(rs.getInt("coordx"));
        task.setCoordY(rs.getInt("coordy"));

        // Simple references — assume already fetched elsewhere or use basic objects
        Plant plant = new Plant();
        plant.setId(rs.getLong("plant_id"));
        task.setPlant(plant);

        User forester = new User();
        forester.setId(rs.getLong("forester_id"));
        task.setForester(forester);

        return task;
    };

    public List<Task> findAll() {
        return jdbc.query("SELECT * FROM tasks", taskRowMapper);
    }

    public Optional<Task> findById(Long id) {
        List<Task> results = jdbc.query("SELECT * FROM tasks WHERE id = ?", taskRowMapper, id);
        return results.stream().findFirst();
    }

    public List<Task> findByForester(Long foresterId) {
        return jdbc.query("SELECT * FROM tasks WHERE forester_id = ?", taskRowMapper, foresterId);
    }

    public void save(Task task) {
        if (task.getId() == null) {
            jdbc.update("""
                INSERT INTO tasks (description, forester_id, action, plant_id, status,
                                   created_at, updated_at, approved_by_owner, completed_by_owner_at,
                                   report_text, coordx, coordy)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
                    task.getDescription(),
                    task.getForester().getId(),
                    task.getAction().name(),
                    task.getPlant().getId(),
                    task.getStatus().name(),
                    Timestamp.valueOf(task.getCreatedAt()),
                    Timestamp.valueOf(task.getUpdatedAt()),
                    task.isApprovedByOwner(),
                    task.getCompletedByOwnerAt() == null ? null : Timestamp.valueOf(task.getCompletedByOwnerAt()),
                    task.getReportText(),
                    task.getCoordX(),
                    task.getCoordY()
            );
        } else {
            jdbc.update("""
                UPDATE tasks SET description=?, forester_id=?, action=?, plant_id=?, status=?,
                                 updated_at=?, approved_by_owner=?, completed_by_owner_at=?,
                                 report_text=?, coordx=?, coordy=? WHERE id=?
            """,
                    task.getDescription(),
                    task.getForester().getId(),
                    task.getAction().name(),
                    task.getPlant().getId(),
                    task.getStatus().name(),
                    Timestamp.valueOf(task.getUpdatedAt()),
                    task.isApprovedByOwner(),
                    task.getCompletedByOwnerAt() == null ? null : Timestamp.valueOf(task.getCompletedByOwnerAt()),
                    task.getReportText(),
                    task.getCoordX(),
                    task.getCoordY(),
                    task.getId()
            );
        }
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM tasks WHERE id = ?", id);
    }
}
