package com.securebank.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.securebank.exception.BankException;
import com.securebank.model.User;
import com.securebank.service.BankService;
import com.securebank.util.InputUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User u = (User) session.getAttribute("user");
            resp.sendRedirect(req.getContextPath() + ("ADMIN".equals(u.getRole()) ? "/admin" : "/dashboard"));
            return;
        }
        InputUtil.moveFlashToRequest(req);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = InputUtil.clean(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        try {
            User user = service.login(email, password);
            if (user == null) {
                req.setAttribute("error", "Invalid email or password");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate(); // new session id after login (prevents session fixation)
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(15 * 60);
            resp.sendRedirect(req.getContextPath() + ("ADMIN".equals(user.getRole()) ? "/admin" : "/dashboard"));
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
