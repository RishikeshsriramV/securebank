package com.securebank.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.securebank.dao.AccountDAO;
import com.securebank.dao.AccountDAOImpl;
import com.securebank.dao.TransactionDAO;
import com.securebank.dao.TransactionDAOImpl;
import com.securebank.dao.UserDAO;
import com.securebank.dao.UserDAOImpl;
import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.BankException;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.model.User;
import com.securebank.util.DBConnection;
import com.securebank.util.PasswordUtil;

/**
 * Business logic layer. Every money movement runs inside ONE JDBC transaction
 * (setAutoCommit(false) -> commit / rollback) so the data is always consistent (ACID).
 */
public class BankService {

    private static final Logger LOG = Logger.getLogger(BankService.class.getName());

    private final UserDAO userDAO = new UserDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final TransactionDAO txnDAO = new TransactionDAOImpl();

    // ------------------------------------------------------------------ users
    public void register(String name, String email, String password) throws BankException {
        if (name.isEmpty() || email.isEmpty() || !email.contains("@")) {
            throw new BankException("Please enter a valid name and email");
        }
        if (password == null || password.length() < 6) {
            throw new BankException("Password must be at least 6 characters");
        }
        try {
            if (userDAO.emailExists(email)) {
                throw new BankException("This email is already registered");
            }
            userDAO.create(new User(name, email, PasswordUtil.hash(password), "CUSTOMER"));
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    /** Returns the user if credentials are valid, otherwise null. */
    public User login(String email, String password) throws BankException {
        try {
            User user = userDAO.findByEmail(email);
            if (user != null && PasswordUtil.verify(password, user.getPasswordHash())) {
                return user;
            }
            return null;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // --------------------------------------------------------------- accounts
    public long openAccount(int userId, String type, double initialDeposit) throws BankException {
        if (!"SAVINGS".equals(type) && !"CURRENT".equals(type)) {
            throw new BankException("Please choose a valid account type");
        }
        Account probe = Account.of(type, 0, userId, 0, Account.ACTIVE, null);
        if (initialDeposit < probe.getMinOpeningDeposit()) {
            throw new BankException("Minimum opening deposit for a " + type.toLowerCase()
                    + " account is Rs." + (int) probe.getMinOpeningDeposit());
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                long accountNo = accountDAO.create(con, userId, type, initialDeposit);
                txnDAO.insert(con, accountNo, "DEPOSIT", initialDeposit, initialDeposit, "Opening deposit");
                con.commit();
                return accountNo;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Account> getAccounts(int userId) throws BankException {
        try {
            return accountDAO.findByUserId(userId);
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ------------------------------------------------------ deposit / withdraw
    public void deposit(int userId, long accountNo, double amount, String remarks) throws BankException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                Account acc = loadOwned(con, accountNo, userId);
                acc.deposit(amount);
                accountDAO.updateBalance(con, accountNo, acc.getBalance());
                txnDAO.insert(con, accountNo, "DEPOSIT", amount, acc.getBalance(), orDefault(remarks, "Cash deposit"));
                con.commit();
            } catch (BankException | SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public void withdraw(int userId, long accountNo, double amount, String remarks) throws BankException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                Account acc = loadOwned(con, accountNo, userId);
                acc.withdraw(amount); // polymorphism: Savings / Current apply their own rule
                accountDAO.updateBalance(con, accountNo, acc.getBalance());
                txnDAO.insert(con, accountNo, "WITHDRAW", amount, acc.getBalance(), orDefault(remarks, "Cash withdrawal"));
                con.commit();
            } catch (BankException | SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // --------------------------------------------------------------- transfer
    /** Overloaded version without remarks (compile-time polymorphism). */
    public void transfer(int userId, long fromAcc, long toAcc, double amount) throws BankException {
        transfer(userId, fromAcc, toAcc, amount, "");
    }

    public void transfer(int userId, long fromAcc, long toAcc, double amount, String remarks)
            throws BankException {
        if (fromAcc == toAcc) {
            throw new BankException("Sender and receiver account cannot be the same");
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // 1. start transaction
            try {
                // Always lock the lower account number first -> two opposite transfers can never deadlock.
                Account lower = accountDAO.findByAccountNo(con, Math.min(fromAcc, toAcc), true);
                Account higher = accountDAO.findByAccountNo(con, Math.max(fromAcc, toAcc), true);
                Account sender = fromAcc < toAcc ? lower : higher;
                Account receiver = fromAcc < toAcc ? higher : lower;

                if (sender == null) {
                    throw new AccountNotFoundException("Sender account " + fromAcc + " not found");
                }
                if (receiver == null) {
                    throw new AccountNotFoundException("Receiver account " + toAcc + " not found");
                }
                if (sender.getUserId() != userId) {
                    throw new BankException("You can transfer only from your own account");
                }

                sender.withdraw(amount);   // business rules checked inside the objects
                receiver.deposit(amount);

                accountDAO.updateBalance(con, fromAcc, sender.getBalance());   // 2. debit
                accountDAO.updateBalance(con, toAcc, receiver.getBalance());   // 3. credit
                String note = remarks == null || remarks.isEmpty() ? "" : " - " + remarks;
                txnDAO.insert(con, fromAcc, "TRANSFER_OUT", amount, sender.getBalance(), "To a/c " + toAcc + note);
                txnDAO.insert(con, toAcc, "TRANSFER_IN", amount, receiver.getBalance(), "From a/c " + fromAcc + note);

                con.commit();              // 4. everything succeeded
            } catch (BankException | SQLException e) {
                con.rollback();            // anything failed -> nothing is saved
                throw e;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // -------------------------------------------------------------- statement
    public List<Transaction> getMiniStatement(int userId, long accountNo) throws BankException {
        try (Connection con = DBConnection.getConnection()) {
            Account acc = accountDAO.findByAccountNo(con, accountNo, false);
            if (acc == null || acc.getUserId() != userId) {
                throw new AccountNotFoundException("Account not found");
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
        try {
            return txnDAO.findRecent(accountNo, 10);
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ------------------------------------------------------------------ admin
    public List<User> getAllCustomers() throws BankException {
        try {
            return userDAO.findAllCustomers();
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Account> getAllAccounts() throws BankException {
        try {
            return accountDAO.findAllWithHolder();
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public void setAccountStatus(long accountNo, String status) throws BankException {
        if (!Account.ACTIVE.equals(status) && !Account.FROZEN.equals(status)) {
            throw new BankException("Invalid status");
        }
        try {
            accountDAO.updateStatus(accountNo, status);
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // ---------------------------------------------------------------- helpers
    private Account loadOwned(Connection con, long accountNo, int userId) throws SQLException, BankException {
        Account acc = accountDAO.findByAccountNo(con, accountNo, true);
        if (acc == null) {
            throw new AccountNotFoundException("Account " + accountNo + " not found");
        }
        if (acc.getUserId() != userId) {
            throw new BankException("You can operate only on your own accounts");
        }
        return acc;
    }

    private String orDefault(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }

    private BankException dbError(SQLException e) {
        LOG.log(Level.SEVERE, "Database error", e);
        return new BankException("Something went wrong. Please try again later.", e);
    }
}
