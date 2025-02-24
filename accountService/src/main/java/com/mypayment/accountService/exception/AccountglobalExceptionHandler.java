package com.mypayment.accountService.exception;

import com.mypayment.accountService.DTO.errorResponseDTO;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class AccountglobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private ObservationRegistry observationRegistry;


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
    @ExceptionHandler(invalidDataException.class)
public ResponseEntity<?> handleInvalidDataException(invalidDataException e, WebRequest webRequest) {
        Observation observation = Observation.start("invalidDataException", observationRegistry);
        try{
        errorResponseDTO errorResponse = new errorResponseDTO(webRequest.getDescription(false), HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    } finally {
            observation.stop();
        }
        }
}
