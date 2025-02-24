package com.mypayment.accountService.accountAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.mypayment.accountService.service.accountService.*(..))")
    public void accountServiceMethods() {}

    @Before("accountServiceMethods()")
    public void logBefore() {
        logger.info("Starting method in AccountService");
    }

    @After("accountServiceMethods()")
    public void logAfter() {
        logger.info("Completed method in AccountService");
    }

    @Around("accountServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before executing: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("After executing: " + joinPoint.getSignature().getName());
        return result;
    }
}