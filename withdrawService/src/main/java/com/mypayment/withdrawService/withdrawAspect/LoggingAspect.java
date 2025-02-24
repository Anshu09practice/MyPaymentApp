// src/main/java/com/mypayment/withdrawService/withdrawAspect/LoggingAspect.java
package com.mypayment.withdrawService.withdrawAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.mypayment.withdrawService.service.withdrawService.*(..))")
    public void withdrawServiceMethods() {}

    @Before("withdrawServiceMethods()")
    public void logBefore() {
        logger.info("Starting method in withdrawService");
    }

    @After("withdrawServiceMethods()")
    public void logAfter() {
        logger.info("Completed method in withdrawService");
    }

    @Around("withdrawServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before executing: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("After executing: " + joinPoint.getSignature().getName());
        return result;
    }
}