package com.securebank.model;

import java.sql.Timestamp;

import com.securebank.exception.BankException;
import com.securebank.exception.InsufficientBalanceException;

/** INHERITANCE + overriding: savings account must always keep a minimum balance. */
public class SavingsAccount extends Account {

    private static final double MIN_BALANCE = 1000;
    private static final double INTEREST_RATE = 0.04; // 4% per year

    public SavingsAccount(long accountNo, int userId, double balance, String status, Timestamp createdAt) {
        super(accountNo, userId, balance, status, createdAt);
    }

    @Override
    public void withdraw(double amount) throws BankException {
        validateAmount(amount);
        ensureActive();
        if (getBalance() - amount < MIN_BALANCE) {
            throw new InsufficientBalanceException(
                    "Savings account must maintain a minimum balance of Rs." + (int) MIN_BALANCE);
        }
        debit(amount);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE;
    }

    @Override
    public String getAccountType() { return "SAVINGS"; }

    @Override
    public double getMinOpeningDeposit() { return MIN_BALANCE; }
}
