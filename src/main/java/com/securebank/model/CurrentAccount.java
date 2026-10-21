package com.securebank.model;

import java.sql.Timestamp;

import com.securebank.exception.BankException;
import com.securebank.exception.InsufficientBalanceException;

/** INHERITANCE + overriding: current account allows an overdraft, but pays no interest. */
public class CurrentAccount extends Account {

    private static final double OVERDRAFT_LIMIT = 5000;

    public CurrentAccount(long accountNo, int userId, double balance, String status, Timestamp createdAt) {
        super(accountNo, userId, balance, status, createdAt);
    }

    @Override
    public void withdraw(double amount) throws BankException {
        validateAmount(amount);
        ensureActive();
        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            throw new InsufficientBalanceException(
                    "Overdraft limit of Rs." + (int) OVERDRAFT_LIMIT + " exceeded");
        }
        debit(amount);
    }

    @Override
    public double calculateInterest() {
        return 0;
    }

    @Override
    public String getAccountType() { return "CURRENT"; }

    @Override
    public double getMinOpeningDeposit() { return 500; }
}
