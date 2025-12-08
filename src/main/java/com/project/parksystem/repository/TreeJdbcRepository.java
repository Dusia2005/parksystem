package com.project.parksystem.repository;

import com.project.parksystem.model.Plant;
import com.project.parksystem.model.Tree;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.project.parksystem.model.Plant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TreeJdbcRepository {

    private final JdbcTemplate jdbc;

    public TreeJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * ВНИМАНИЕ: все SELECT'ы возвращают колонки из trees + явные alias-ы для plant.name и plant.image_filename
     * чтобы rowMapper мог их прочитать.
     */
    private final RowMapper<Tree> treeMapper = (rs, rowNum) -> {
        Tree tree = new Tree();
        tree.setId(rs.getLong("id"));

        // колонки в БД: coord_x, coord_y
        tree.setCoordX(rs.getInt("coordx"));
        tree.setCoordY(rs.getInt("coordy"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) tree.setCreatedAt(createdTs.toLocalDateTime());

        // статус/last_action/is_deleted могут быть nullable
        try {
            String status = rs.getString("status");
            if (status != null) tree.setStatus(status);
        } catch (Exception ignored) {}

        Timestamp lastActionTs = null;
        try { lastActionTs = rs.getTimestamp("last_action_at"); } catch (Exception ignored) {}
        if (lastActionTs != null) tree.setLastActionAt(lastActionTs.toLocalDateTime());

        try {
            boolean isDeleted = rs.getBoolean("is_deleted");
            tree.setDeleted(isDeleted);
        } catch (Exception ignored) {}

        // Plant: используя alias'ы plant_name и image_filename (см. SQL ниже)
        Plant plant = new Plant();
        try {
            long plantId = rs.getLong("plant_id");
            if (!rs.wasNull()) plant.setId(plantId);
        } catch (Exception ignored) {}

        try {
            String plantName = rs.getString("plant_name");
            if (plantName != null) plant.setName(plantName);
        } catch (Exception ignored) {}

        try {
            String img = rs.getString("image_filename");
            if (img != null) plant.setImageFilename(img);
        } catch (Exception ignored) {}

        tree.setPlant(plant);
        return tree;
    };

    public List<Tree> findAll() {
        String sql = """
            SELECT t.*, p.name AS plant_name, p.image_filename
            FROM trees t
            LEFT JOIN plants p ON t.plant_id = p.id
            WHERE COALESCE(t.is_deleted, false) = false
            """;
        return jdbc.query(sql, treeMapper);
    }

    public Optional<Tree> findById(Long id) {
        String sql = """
            SELECT t.*, p.name AS plant_name, p.image_filename
            FROM trees t
            LEFT JOIN plants p ON t.plant_id = p.id
            WHERE t.id = ?
            """;
        List<Tree> list = jdbc.query(sql, treeMapper, id);
        return list.stream().findFirst();
    }

    public Optional<Tree> findRawById(Long id) {
        // без фильтра is_deleted
        String sql = """
            SELECT t.*, p.name AS plant_name, p.image_filename
            FROM trees t
            LEFT JOIN plants p ON t.plant_id = p.id
            WHERE t.id = ?
            """;
        List<Tree> list = jdbc.query(sql, treeMapper, id);
        return list.stream().findFirst();
    }

    public Tree saveAndReturn(Tree tree) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                INSERT INTO trees (plant_id, coordx, coordy, status, last_action_at, is_deleted, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);

            Long plantId = tree.getPlant() != null ? tree.getPlant().getId() : null;
            if (plantId != null) ps.setLong(1, plantId); else ps.setNull(1, Types.BIGINT);
            ps.setInt(2, tree.getCoordX() == null ? 0 : tree.getCoordX());
            ps.setInt(3, tree.getCoordY() == null ? 0 : tree.getCoordY());
            ps.setString(4, tree.getStatus() == null ? null : tree.getStatus());
            ps.setTimestamp(5, tree.getLastActionAt() == null ? null : Timestamp.valueOf(tree.getLastActionAt()));
            ps.setBoolean(6, tree.isDeleted());
            ps.setTimestamp(7, tree.getCreatedAt() == null ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(tree.getCreatedAt()));

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            tree.setId(key.longValue());
        }
        return tree;
    }

    public void update(Tree tree) {
        jdbc.update("""
                UPDATE trees SET plant_id=?, coordx=?, coordy=?, status=?, last_action_at=?, is_deleted=? WHERE id=?
                """,
                tree.getPlant() != null && tree.getPlant().getId() != null ? tree.getPlant().getId() : null,
                tree.getCoordX(),
                tree.getCoordY(),
                tree.getStatus() != null ? tree.getStatus() : null,
                tree.getLastActionAt() == null ? null : Timestamp.valueOf(tree.getLastActionAt()),
                tree.isDeleted(),
                tree.getId()
        );
    }

    public void delete(Long id) {
        jdbc.update("DELETE FROM trees WHERE id = ?", id);
    }

    public List<Tree> findAllInRadius(int x, int y, int treeRadius) {
        String sql = """
            SELECT t.*, p.name AS plant_name, p.image_filename
            FROM trees t
            LEFT JOIN plants p ON t.plant_id = p.id
            WHERE COALESCE(t.is_deleted, false) = false
              AND POWER(t.coordx - ?, 2) + POWER(t.coordy - ?, 2) <= POWER(?, 2)
            """;
        return jdbc.query(sql, treeMapper, x, y, treeRadius);
    }

    public List<Tree> findAllInBBox(int minX, int maxX, int minY, int maxY) {
        String sql = """
            SELECT t.*, p.name AS plant_name, p.image_filename
            FROM trees t
            LEFT JOIN plants p ON t.plant_id = p.id
            WHERE COALESCE(t.is_deleted, false) = false
              AND t.coordx BETWEEN ? AND ?
              AND t.coordy BETWEEN ? AND ?
            """;
        return jdbc.query(sql, treeMapper, minX, maxX, minY, maxY);
    }
}
