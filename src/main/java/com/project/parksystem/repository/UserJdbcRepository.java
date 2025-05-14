package com.project.parksystem.repository;

import com.project.parksystem.config.ConnectionPool;
import com.project.parksystem.model.Role;
import com.project.parksystem.model.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями через JDBC.
 */
@Repository
public class UserJdbcRepository {

    /**
     * Найти пользователя по ID.
     */
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске пользователя по ID", e);
        }
    }

    /**
     * Найти пользователя по имени.
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске пользователя по имени", e);
        }
    }

    /**
     * Получить всех пользователей по роли.
     */
    public List<User> findAllByRole(Role role) {
        String sql = "SELECT * FROM users WHERE role = ?";
        List<User> users = new ArrayList<>();

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, role.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске пользователей по роли", e);
        }
    }

    /**
     * Сохранить нового пользователя.
     */
    public void save(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    /**
     * Маппинг строки результата запроса в объект User.
     */
    private User mapRowToUser(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));
        return user;
    }
}
