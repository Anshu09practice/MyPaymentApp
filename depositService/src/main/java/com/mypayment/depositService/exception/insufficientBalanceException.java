package com.mypayment.depositService.exception;

public class insufficientBalanceException extends RuntimeException {
    public insufficientBalanceException(String message) {
        super(message);
    }
}
