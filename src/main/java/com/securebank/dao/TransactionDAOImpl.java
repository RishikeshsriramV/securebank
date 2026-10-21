package com.securebank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.securebank.model.Transaction;
import com.securebank.util.DBConnection;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public void insert(Connection con, long accountNo, String type, double amount,
                       double balanceAfter, String remarks) throws SQLException {
        String sql = "INSERT INTO transactions (account_no, type, amount, balance_after, remarks) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setDouble(4, balanceAfter);
            ps.setString(5, remarks);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaction> findRecent(long accountNo, int limit) throws SQLException {
        String sql = "SELECT txn_id, account_no, type, amount, balance_after, txn_time, remarks "
                + "FROM transactions WHERE account_no = ? ORDER BY txn_id DESC LIMIT ?";
        List<Transaction> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, accountNo);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setTxnId(rs.getLong("txn_id"));
                    t.setAccountNo(rs.getLong("account_no"));
                    t.setType(rs.getString("type"));
                    t.setAmount(rs.getDouble("amount"));
                    t.setBalanceAfter(rs.getDouble("balance_after"));
                    t.setTxnTime(rs.getTimestamp("txn_time"));
                    t.setRemarks(rs.getString("remarks"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}
