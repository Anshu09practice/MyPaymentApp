// src/main/java/com/mypayment/TransferService/transferAspect/LoggingAspect.java
package com.mypayment.TransferService.transferAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.mypayment.TransferService.service.TransferService.*(..))")
    public void transferServiceMethods() {}

    @Before("transferServiceMethods()")
    public void logBefore() {
        logger.info("Starting method in TransferService");
    }

    @After("transferServiceMethods()")
    public void logAfter() {
        logger.info("Completed method in TransferService");
    }

    @Around("transferServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before executing: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("After executing: " + joinPoint.getSignature().getName());
        return result;
    }
}