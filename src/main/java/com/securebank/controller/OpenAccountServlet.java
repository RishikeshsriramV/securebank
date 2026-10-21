package com.securebank.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.securebank.exception.BankException;
import com.securebank.model.User;
import com.securebank.service.BankService;
import com.securebank.util.InputUtil;

@WebServlet("/open-account")
public class OpenAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/openAccount.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            String type = InputUtil.clean(req.getParameter("type"));
            double initial = InputUtil.parseAmount(req.getParameter("initialDeposit"));
            long accountNo = service.openAccount(user.getUserId(), type, initial);
            req.getSession().setAttribute("flash", "Account opened successfully. Your account number is " + accountNo);
            resp.sendRedirect(req.getContextPath() + "/dashboard"); // Post-Redirect-Get
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/openAccount.jsp").forward(req, resp);
        }
    }
}
