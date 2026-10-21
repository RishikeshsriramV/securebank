package com.securebank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.securebank.model.Account;
import com.securebank.util.DBConnection;

public class AccountDAOImpl implements AccountDAO {

    private static final String COLUMNS =
            "account_no, user_id, account_type, balance, status, created_at";

    @Override
    public long create(Connection con, int userId, String type, double balance) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_type, balance) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, balance);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Account number was not generated");
            }
        }
    }

    /** forUpdate=true -> SELECT ... FOR UPDATE: row lock so two requests cannot change the balance together. */
    @Override
    public Account findByAccountNo(Connection con, long accountNo, boolean forUpdate) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM accounts WHERE account_no = ?"
                + (forUpdate ? " FOR UPDATE" : "");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void updateBalance(Connection con, long accountNo, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setLong(2, accountNo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Account> findByUserId(int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM accounts WHERE user_id = ? ORDER BY account_no";
        List<Account> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** SQL JOIN of accounts + users for the admin screen. */
    @Override
    public List<Account> findAllWithHolder() throws SQLException {
        String sql = "SELECT a.account_no, a.user_id, a.account_type, a.balance, a.status, a.created_at, "
                + "u.name FROM accounts a JOIN users u ON a.user_id = u.user_id ORDER BY a.account_no";
        List<Account> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Account a = map(rs);
                a.setHolderName(rs.getString("name"));
                list.add(a);
            }
        }
        return list;
    }

    @Override
    public void updateStatus(long accountNo, String status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, accountNo);
            ps.executeUpdate();
        }
    }

    private Account map(ResultSet rs) throws SQLException {
        return Account.of(rs.getString("account_type"), rs.getLong("account_no"),
                rs.getInt("user_id"), rs.getDouble("balance"),
                rs.getString("status"), rs.getTimestamp("created_at"));
    }
}
