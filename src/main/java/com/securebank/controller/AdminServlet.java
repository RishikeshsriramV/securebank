package com.securebank.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.securebank.exception.BankException;
import com.securebank.model.Account;
import com.securebank.service.BankService;
import com.securebank.util.InputUtil;

/** Admin only - AuthFilter returns 403 for everyone else. */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BankService service = new BankService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("customers", service.getAllCustomers());
            req.setAttribute("accounts", service.getAllAccounts());
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
        }
        InputUtil.moveFlashToRequest(req);
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long accountNo = InputUtil.parseLong(req.getParameter("accountNo"), "account");
            boolean freeze = "freeze".equals(req.getParameter("action"));
            service.setAccountStatus(accountNo, freeze ? Account.FROZEN : Account.ACTIVE);
            req.getSession().setAttribute("flash",
                    "Account " + accountNo + (freeze ? " frozen" : " activated"));
        } catch (BankException e) {
            req.getSession().setAttribute("flash", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}
