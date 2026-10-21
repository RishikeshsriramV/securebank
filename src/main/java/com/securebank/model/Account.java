package com.securebank.model;

import java.sql.Timestamp;

import com.securebank.exception.BankException;

/**
 * ABSTRACTION  : common state + behaviour of every account; rules that differ are abstract.
 * ENCAPSULATION: balance is private - it changes only through deposit()/withdraw().
 */
public abstract class Account {

    public static final String ACTIVE = "ACTIVE";
    public static final String FROZEN = "FROZEN";
    private static final double MAX_PER_TRANSACTION = 10_000_000;

    private final long accountNo;
    private final int userId;
    private double balance;
    private String status;
    private final Timestamp createdAt;
    private String holderName; // filled only for admin views (JOIN with users)

    protected Account(long accountNo, int userId, double balance, String status, Timestamp createdAt) {
        this.accountNo = accountNo;
        this.userId = userId;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    /** Factory method - creates the right subclass from the account type stored in DB. */
    public static Account of(String type, long accountNo, int userId, double balance,
                             String status, Timestamp createdAt) {
        if ("SAVINGS".equalsIgnoreCase(type)) {
            return new SavingsAccount(accountNo, userId, balance, status, createdAt);
        } else if ("CURRENT".equalsIgnoreCase(type)) {
            return new CurrentAccount(accountNo, userId, balance, status, createdAt);
        }
        throw new IllegalArgumentException("Unknown account type: " + type);
    }

    // ---- common behaviour (same for every account type) ----
    public void deposit(double amount) throws BankException {
        validateAmount(amount);
        ensureActive();
        balance = round2(balance + amount);
    }

    // ---- behaviour that differs per account type (POLYMORPHISM) ----
    public abstract void withdraw(double amount) throws BankException;

    public abstract double calculateInterest();

    public abstract String getAccountType();

    public abstract double getMinOpeningDeposit();

    // ---- helpers for subclasses ----
    protected void validateAmount(double amount) throws BankException {
        if (amount <= 0) {
            throw new BankException("Amount must be greater than zero");
        }
        if (amount > MAX_PER_TRANSACTION) {
            throw new BankException("Amount exceeds the per-transaction limit of Rs.10,000,000");
        }
    }

    protected void ensureActive() throws BankException {
        if (FROZEN.equals(status)) {
            throw new BankException("Account " + accountNo + " is frozen. Please contact the bank.");
        }
    }

    protected void debit(double amount) {
        balance = round2(balance - amount);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ---- getters / setters ----
    public long getAccountNo() { return accountNo; }
    public int getUserId() { return userId; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public double getAnnualInterest() { return calculateInterest(); }
}
