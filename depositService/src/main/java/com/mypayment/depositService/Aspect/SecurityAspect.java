package com.mypayment.depositService.Aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Pointcut("execution(* com.mypayment.depositService.service.DepositService.*(..))")
    public void depositServiceMethods() {}

    @Before("depositServiceMethods()")
    public void checkSecurity() {
        // Implement security checks here
        System.out.println("Security check performed");
    }
}