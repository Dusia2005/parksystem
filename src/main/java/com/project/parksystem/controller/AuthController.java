package com.project.parksystem.controller;

import com.project.parksystem.model.Role;
import com.project.parksystem.model.User;
import com.project.parksystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class);


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        logger.info("Открыта страница входа");
        return "login"; // templates/login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        logger.info("Открыта страница регистрации");

        return "register"; // templates/register.html
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        if (authentication != null) {
            logger.info("Пользователь {} вошёл с ролью: {}", authentication.getName(), authentication.getAuthorities());
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
                model.addAttribute("role", "OWNER");
                model.addAttribute("username", authentication.getName());
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_FORESTER"))) {
                model.addAttribute("role", "FORESTER");
                model.addAttribute("username", authentication.getName());
            } else {
                model.addAttribute("role", "USER"); // вдруг какая-то другая роль
            }
        } else {
            logger.info("Гость посетил главную страницу");
            model.addAttribute("role", "GUEST");
        }
        return "index";
    }


    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String secretWord,
            Model model) {
        logger.info("Попытка регистрации пользователя: {}", username);

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            logger.warn("Пароли не совпадают для пользователя: {}", username);
            model.addAttribute("error", "Пароли не совпадают!");
            return "register";
        }

        // Определяем роль по секретному слову
        Role role;
        if ("ВЛАДЕЛЕЦ".equalsIgnoreCase(secretWord)) {
            role = Role.OWNER;
        } else if ("ЛЕСНИК".equalsIgnoreCase(secretWord)) {
            role = Role.FORESTER;
        } else {
            logger.warn("Неверное секретное слово при регистрации пользователя: {}", username);
            model.addAttribute("error", "Неверное секретное слово!");
            return "register";
        }

        // Создаём пользователя
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        // Сохраняем в базе
        try {
            userService.registerUser(user);
            logger.info("Пользователь {} успешно зарегистрирован с ролью {}", username, role);
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            model.addAttribute("error", "Ошибка: пользователь уже существует!");
            return "register";
        }

        return "redirect:/login";
    }
}
