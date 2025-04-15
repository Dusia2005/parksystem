package com.project.parksystem.service;

import com.project.parksystem.model.User;
import com.project.parksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    // Уже есть
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        System.out.println("Регистрируем пользователя: " + user.getUsername() + " с ролью " + user.getRole());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь уже существует!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Хешируем пароль
        userRepository.save(user);

        System.out.println("Пользователь сохранен в БД: " + user.getUsername());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ✅ Говорим Spring Security, как загружать пользователя
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // Устанавливаем роль пользователя
                .build();
    }
}