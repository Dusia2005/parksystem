package com.project.parksystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Фильтр, проверяющий наличие активной сессии для защищённых страниц.
 */
@WebFilter(urlPatterns = {"/dashboard", "/tasks/*"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean loggedIn = session != null && session.getAttribute("username") != null;

        if (!loggedIn) {
            res.sendRedirect("/login"); // редирект если пользователь не вошёл
        } else {
            chain.doFilter(request, response); // продолжить выполнение
        }
    }
}
