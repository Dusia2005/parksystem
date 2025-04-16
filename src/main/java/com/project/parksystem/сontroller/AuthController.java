package com.project.parksystem.сontroller;

import com.project.parksystem.model.Role;
import com.project.parksystem.model.User;
import com.project.parksystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
            return "redirect:/tasks";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FORESTER"))) {
            return "redirect:/reports";
        }
        return "index"; // если просто гость
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String secretWord,
            Model model) {

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
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
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка: пользователь уже существует!");
            return "register";
        }

        return "redirect:/login";
    }
}
