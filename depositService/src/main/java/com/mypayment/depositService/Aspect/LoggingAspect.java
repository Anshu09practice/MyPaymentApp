package com.mypayment.depositService.Aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.mypayment.depositService.service.DepositService.*(..))")
    public void depositServiceMethods() {}

    @Before("depositServiceMethods()")
    public void logBefore() {
        logger.info("Starting method in DepositService");
    }

    @After("depositServiceMethods()")
    public void logAfter() {
        logger.info("Completed method in DepositService");
    }

    @Around("depositServiceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before executing: " + joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        logger.info("After executing: " + joinPoint.getSignature().getName());
        return result;
    }
}