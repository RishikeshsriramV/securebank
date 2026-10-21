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

/** One controller for /deposit, /withdraw and /transfer (the URL decides the action). */
@WebServlet({"/deposit", "/withdraw", "/transfer"})
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        showForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String path = req.getServletPath();
        try {
            long accountNo = InputUtil.parseLong(req.getParameter("accountNo"), "account");
            double amount = InputUtil.parseAmount(req.getParameter("amount"));
            String remarks = InputUtil.clean(req.getParameter("remarks"));
            String amt = String.format("%.2f", amount);
            String message;

            switch (path) {
                case "/deposit":
                    service.deposit(user.getUserId(), accountNo, amount, remarks);
                    message = "Rs." + amt + " deposited successfully";
                    break;
                case "/withdraw":
                    service.withdraw(user.getUserId(), accountNo, amount, remarks);
                    message = "Rs." + amt + " withdrawn successfully";
                    break;
                default: // /transfer
                    long toAccount = InputUtil.parseLong(req.getParameter("toAccount"), "receiver account number");
                    service.transfer(user.getUserId(), accountNo, toAccount, amount, remarks);
                    message = "Rs." + amt + " transferred to account " + toAccount;
                    break;
            }
            req.getSession().setAttribute("flash", message);
            resp.sendRedirect(req.getContextPath() + "/dashboard"); // Post-Redirect-Get
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
            showForm(req, resp);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("accounts", service.getAccounts(user.getUserId()));
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
        }
        String action = req.getServletPath().substring(1); // deposit | withdraw | transfer
        req.setAttribute("action", action);
        req.setAttribute("mode", action.toUpperCase());
        req.getRequestDispatcher("/WEB-INF/views/transaction.jsp").forward(req, resp);
    }
}
