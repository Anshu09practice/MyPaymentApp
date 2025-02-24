// src/main/java/com/mypayment/TransferService/transferAspect/TransactionManagementAspect.java
package com.mypayment.TransferService.transferAspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class TransactionManagementAspect {

    @Pointcut("execution(* com.mypayment.TransferService.service.TransferService.*(..))")
    public void transferServiceMethods() {}

    @Before("transferServiceMethods()")
    @Transactional
    public void startTransaction() {
        // Transaction management is handled by Spring, so no need to manually start a transaction
        System.out.println("Transaction started");
    }

    @After("transferServiceMethods()")
    @Transactional
    public void endTransaction() {
        // Transaction management is handled by Spring, so no need to manually end a transaction
        System.out.println("Transaction ended");
    }
}