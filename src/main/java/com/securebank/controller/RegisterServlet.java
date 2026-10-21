package com.securebank.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.securebank.exception.BankException;
import com.securebank.service.BankService;
import com.securebank.util.InputUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = InputUtil.clean(req.getParameter("name"));
        String email = InputUtil.clean(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        try {
            if (password == null || !password.equals(confirm)) {
                throw new BankException("Passwords do not match");
            }
            service.register(name, email, password);
            req.getSession(true).setAttribute("flash", "Registration successful. Please login.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
