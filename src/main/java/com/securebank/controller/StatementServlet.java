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

@WebServlet("/statement")
public class StatementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            req.setAttribute("accounts", service.getAccounts(user.getUserId()));
            String no = InputUtil.clean(req.getParameter("accountNo"));
            if (!no.isEmpty()) {
                long accountNo = InputUtil.parseLong(no, "account");
                req.setAttribute("transactions", service.getMiniStatement(user.getUserId(), accountNo));
                req.setAttribute("selected", accountNo);
            }
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/statement.jsp").forward(req, resp);
    }
}
