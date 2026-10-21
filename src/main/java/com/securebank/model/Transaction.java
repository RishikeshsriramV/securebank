package com.securebank.model;

import java.sql.Timestamp;

public class Transaction {
    private long txnId;
    private long accountNo;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    private double amount;
    private double balanceAfter;
    private Timestamp txnTime;
    private String remarks;

    public long getTxnId() { return txnId; }
    public void setTxnId(long txnId) { this.txnId = txnId; }
    public long getAccountNo() { return accountNo; }
    public void setAccountNo(long accountNo) { this.accountNo = accountNo; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }
    public Timestamp getTxnTime() { return txnTime; }
    public void setTxnTime(Timestamp txnTime) { this.txnTime = txnTime; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
