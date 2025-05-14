package com.project.parksystem.controller;

import com.project.parksystem.model.Role;
import com.project.parksystem.model.User;
import com.project.parksystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для обработки запросов аутентификации и регистрации пользователей.
 */
@Controller
public class AuthController {

    private static final Logger LOGGER = LogManager.getLogger(AuthController.class);
    private static final String ROLE_OWNER = "ROLE_OWNER";
    private static final String ROLE_FORESTER = "ROLE_FORESTER";
    private static final String OWNER_SECRET = "ВЛАДЕЛЕЦ";
    private static final String FORESTER_SECRET = "ЛЕСНИК";
    private static final String ERROR_ATTR = "error";
    private static final String ROLE_ATTR = "role";
    private static final String USERNAME_ATTR = "username";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userService сервис для работы с пользователями
     * @param passwordEncoder кодировщик паролей
     */
    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        LOGGER.info("Открыта страница входа");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        LOGGER.info("Открыта страница регистрации");
        return "register";
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication, HttpSession session) {
        if (authentication != null) {
            LOGGER.info("Пользователь {} вошёл с ролью: {}",
                    authentication.getName(), authentication.getAuthorities());

            session.setAttribute(USERNAME_ATTR, authentication.getName());
            setUserRoleAttributes(authentication, model, session);
        } else {
            LOGGER.info("Гость посетил главную страницу");
            setGuestAttributes(model, session);
        }
        return "index";
    }

    /**
     * Устанавливает атрибуты роли пользователя.
     */
    private void setUserRoleAttributes(Authentication authentication, Model model, HttpSession session) {
        String role = determineUserRole(authentication);
        session.setAttribute(ROLE_ATTR, role);
        model.addAttribute(ROLE_ATTR, role);
        model.addAttribute(USERNAME_ATTR, authentication.getName());
    }

    /**
     * Определяет роль пользователя на основе authorities.
     */
    private String determineUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_OWNER)) ? "OWNER" :
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_FORESTER)) ? "FORESTER" : "USER";
    }

    /**
     * Устанавливает атрибуты для гостя.
     */
    private void setGuestAttributes(Model model, HttpSession session) {
        session.setAttribute(ROLE_ATTR, "GUEST");
        model.addAttribute(ROLE_ATTR, "GUEST");
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String secretWord,
            Model model) {

        LOGGER.info("Попытка регистрации пользователя: {}", username);

        if (!password.equals(confirmPassword)) {
            LOGGER.warn("Пароли не совпадают для пользователя: {}", username);
            model.addAttribute(ERROR_ATTR, "Пароли не совпадают!");
            return "register";
        }

        Role role = determineRoleBySecretWord(secretWord);
        if (role == null) {
            LOGGER.warn("Неверное секретное слово при регистрации пользователя: {}", username);
            model.addAttribute(ERROR_ATTR, "Неверное секретное слово!");
            return "register";
        }

        return registerNewUser(username, password, role, model);
    }

    /**
     * Определяет роль пользователя по секретному слову.
     */
    private Role determineRoleBySecretWord(String secretWord) {
        if (OWNER_SECRET.equalsIgnoreCase(secretWord)) {
            return Role.OWNER;
        } else if (FORESTER_SECRET.equalsIgnoreCase(secretWord)) {
            return Role.FORESTER;
        }
        return null;
    }

    /**
     * Регистрирует нового пользователя.
     */
    private String registerNewUser(String username, String password, Role role, Model model) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        try {
            userService.registerUser(user);
            LOGGER.info("Пользователь {} успешно зарегистрирован с ролью {}", username, role);
            return "redirect:/login";
        } catch (Exception e) {
            LOGGER.error("Ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            model.addAttribute(ERROR_ATTR, "Ошибка: пользователь уже существует!");
            return "register";
        }
    }
}