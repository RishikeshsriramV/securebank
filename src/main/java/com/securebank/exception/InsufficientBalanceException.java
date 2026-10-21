package com.securebank.exception;

public class InsufficientBalanceException extends BankException {
    private static final long serialVersionUID = 1L;

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
