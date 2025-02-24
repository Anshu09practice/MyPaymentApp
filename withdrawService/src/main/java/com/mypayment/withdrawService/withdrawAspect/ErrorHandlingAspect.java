// src/main/java/com/mypayment/withdrawService/withdrawAspect/ErrorHandlingAspect.java
package com.mypayment.withdrawService.withdrawAspect;

import com.mypayment.withdrawService.DTO.errorResponseDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class ErrorHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @Pointcut("execution(* com.mypayment.withdrawService.service.withdrawService.*(..))")
    public void withdrawServiceMethods() {}

    @AfterThrowing(pointcut = "withdrawServiceMethods()", throwing = "ex")
    public ResponseEntity<errorResponseDTO> handleException(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), ex.getCause() != null ? ex.getCause() : "NULL");

        errorResponseDTO errorResponse = new errorResponseDTO(
                joinPoint.getSignature().toShortString(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}