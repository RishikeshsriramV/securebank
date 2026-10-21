package com.securebank.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.securebank.model.Transaction;

public interface TransactionDAO {
    void insert(Connection con, long accountNo, String type, double amount,
                double balanceAfter, String remarks) throws SQLException;

    List<Transaction> findRecent(long accountNo, int limit) throws SQLException;
}
