package com.securebank.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.securebank.model.Account;

public interface AccountDAO {
    /** Methods taking a Connection take part in the caller's JDBC transaction. */
    long create(Connection con, int userId, String type, double balance) throws SQLException;

    Account findByAccountNo(Connection con, long accountNo, boolean forUpdate) throws SQLException;

    void updateBalance(Connection con, long accountNo, double newBalance) throws SQLException;

    List<Account> findByUserId(int userId) throws SQLException;

    List<Account> findAllWithHolder() throws SQLException;

    void updateStatus(long accountNo, String status) throws SQLException;
}
