package com.mypayment.accountService.exception;

public class invalidDataException extends RuntimeException{

    public invalidDataException(String message) {
        super(message);
    }

}
