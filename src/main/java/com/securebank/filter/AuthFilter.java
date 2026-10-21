package com.securebank.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.securebank.model.User;

/**
 * Runs before EVERY request: blocks pages for users who are not logged in,
 * protects /admin for ADMIN role only, and disables caching of private pages.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList("/login", "/register", "/index.jsp");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();
        boolean isPublic = PUBLIC_PATHS.contains(path) || path.startsWith("/css/");

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (!isPublic && user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (path.startsWith("/admin") && (user == null || !"ADMIN".equals(user.getRole()))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access only");
            return;
        }
        if (!isPublic) {
            // so the browser Back button cannot show private pages after logout
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }
        chain.doFilter(req, res);
    }
}
