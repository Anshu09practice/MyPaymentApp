package com.mypayment.depositService.exception;

public class retryException extends RuntimeException{
    public retryException(String message) {
        super(message);
    }
}
