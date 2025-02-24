package com.mypayment.depositService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class invalidDataException extends RuntimeException {
    public invalidDataException(String message) {
        super(message);
    }
}
