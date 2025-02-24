// src/main/java/com/mypayment/withdrawService/withdrawAspect/TransactionManagementAspect.java
package com.mypayment.withdrawService.withdrawAspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class TransactionManagementAspect {

    @Pointcut("execution(* com.mypayment.withdrawService.service.withdrawService.*(..))")
    public void withdrawServiceMethods() {}

    @Before("withdrawServiceMethods()")
    @Transactional
    public void startTransaction() {
        // Transaction management is handled by Spring, so no need to manually start a transaction
        System.out.println("Transaction started");
    }

    @After("withdrawServiceMethods()")
    @Transactional
    public void endTransaction() {
        // Transaction management is handled by Spring, so no need to manually end a transaction
        System.out.println("Transaction ended");
    }
}