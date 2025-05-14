package com.project.parksystem.service;

import com.project.parksystem.model.Role;
import com.project.parksystem.model.User;
import com.project.parksystem.repository.UserJdbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями и интеграции с Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserJdbcRepository userJdbcRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserJdbcRepository userDao, PasswordEncoder passwordEncoder) {
        this.userJdbcRepository = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllForesters() {
        try {
            return userJdbcRepository.findAllByRole(Role.FORESTER);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении лесников", e);
        }
    }

    public User getUserById(Long id) {
        return userJdbcRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    /**
     * Регистрирует нового пользователя.
     */
    public void registerUser(User user) {
        try {
            if (userJdbcRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new RuntimeException("Пользователь уже существует!");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userJdbcRepository.save(user);
            System.out.println("Пользователь сохранен в БД: " + user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при регистрации пользователя", e);
        }
    }

    public User findByUsername(String username) {
        return userJdbcRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    /**
     * Загрузка пользователя по имени для Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userJdbcRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
