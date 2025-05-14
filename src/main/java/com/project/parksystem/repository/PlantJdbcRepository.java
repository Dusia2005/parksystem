package com.project.parksystem.repository;

import com.project.parksystem.config.ConnectionPool;
import com.project.parksystem.model.Plant;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с таблицей plants через JDBC.
 */
@Repository
public class PlantJdbcRepository {

    /**
     * Получить список всех растений.
     */
    public List<Plant> findAll() {
        String sql = "SELECT * FROM plants";
        List<Plant> plants = new ArrayList<>();

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                plants.add(mapRowToPlant(rs));
            }
            return plants;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка растений", e);
        }
    }

    /**
     * Найти растение по ID.
     */
    public Optional<Plant> findById(Long id) {
        String sql = "SELECT * FROM plants WHERE id = ?";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToPlant(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске растения по ID", e);
        }
    }

    /**
     * Найти растение по имени, без учета регистра.
     */
    public Optional<Plant> findByNameIgnoreCase(String name) {
        String sql = "SELECT * FROM plants WHERE LOWER(name) = LOWER(?)";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToPlant(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске растения по имени", e);
        }
    }

    /**
     * Проверка существования растения по имени (без учета регистра).
     */
    public boolean existsByNameIgnoreCase(String name) {
        return findByNameIgnoreCase(name).isPresent();
    }

    /**
     * Сохранение нового растения.
     */
    public void save(Plant plant) {
        String sql = "INSERT INTO plants (name) VALUES (?)";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, plant.getName());
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении растения", e);
        }
    }

    /**
     * Удалить растение по ID.
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM plants WHERE id = ?";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении растения", e);
        }
    }

    /**
     * Маппинг строки результата запроса в объект Plant.
     */
    private Plant mapRowToPlant(ResultSet rs) throws Exception {
        Plant plant = new Plant();
        plant.setId(rs.getLong("id"));
        plant.setName(rs.getString("name"));
        return plant;
    }
}
