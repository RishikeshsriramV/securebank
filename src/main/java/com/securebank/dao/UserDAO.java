package com.securebank.dao;

import java.sql.SQLException;
import java.util.List;

import com.securebank.model.User;

/** Contract only - implementation can change (MySQL -> Oracle) without touching the service layer. */
public interface UserDAO {
    boolean emailExists(String email) throws SQLException;

    int create(User user) throws SQLException;

    User findByEmail(String email) throws SQLException;

    List<User> findAllCustomers() throws SQLException;
}
