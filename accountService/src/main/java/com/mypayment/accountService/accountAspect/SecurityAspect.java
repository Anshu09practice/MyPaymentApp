package com.mypayment.accountService.accountAspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Pointcut("execution(* com.mypayment.accountService.service.accountService.*(..))")
    public void accountServiceMethods() {}

    @Before("accountServiceMethods()")
    public void checkSecurity() {
        // Implement security checks here
        System.out.println("Security check performed");
    }
}