package com.mypayment.withdrawService.exception;


import com.mypayment.withdrawService.DTO.errorResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class withdrawglobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        List<ObjectError> errorList= ex.getBindingResult().getAllErrors();
        errorList.forEach(objectError -> {
            String fieldName = objectError.getObjectName();
            String errorMessage = objectError.getDefaultMessage();
            error.put(fieldName,errorMessage);
        });
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e, WebRequest webRequest) {
        errorResponseDTO errorResponse = new errorResponseDTO(webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(invalidDataException.class)
    public ResponseEntity<?> handleInvalidDataException(invalidDataException e, WebRequest webRequest) {
        errorResponseDTO errorResponse = new errorResponseDTO(webRequest.getDescription(false), HttpStatus.BAD_REQUEST,e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(insufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalanceException(insufficientBalanceException e, WebRequest webRequest) {
        errorResponseDTO errorResponse = new errorResponseDTO(webRequest.getDescription(false), HttpStatus.BAD_REQUEST,e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
