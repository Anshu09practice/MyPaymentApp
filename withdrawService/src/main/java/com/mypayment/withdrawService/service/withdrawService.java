package com.mypayment.withdrawService.service;


import com.mypayment.withdrawService.DTO.withdrawDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class withdrawService {

    @Autowired
    private KafkaTemplate<String, Object> sendUpdatetoAccountServicewithdraw;
    private static final String withdrawUpdateTopic = "withdraw-funds";
    private final Counter withdrawCounter;
    private final ThreadPoolExecutor executorServicewithdraw;
    private final Lock lock = new ReentrantLock();
    private static final Logger logger = LoggerFactory.getLogger(withdrawService.class);
    private final MeterRegistry meterRegistry;


    public withdrawService(MeterRegistry meterRegistry){
        this.meterRegistry = meterRegistry;
        this.withdrawCounter = meterRegistry.counter("withdrawService.withdraw");
        this.executorServicewithdraw = new ThreadPoolExecutor(1,
                Integer.MAX_VALUE,
                60L,
                java.util.concurrent.TimeUnit.SECONDS,
                new java.util.concurrent.LinkedBlockingQueue<>()
        );
        ExecutorServiceMetrics.monitor(meterRegistry,this.executorServicewithdraw,"withdrawService.executorService");

    }



    public void withdrawMoney(String accountNumber, double amount) {
        withdrawCounter.increment();
        executorServicewithdraw.submit(() -> {
            lock.lock();
            try {
                logger.info("Account number is {} and the amount withdraw is {}", accountNumber, amount);
                withdrawDTO withdrawDTO = new withdrawDTO(accountNumber, amount, "WITHDRAW", "PENDING", LocalDateTime.now());
                sendUpdatetoAccountServicewithdraw.send(withdrawUpdateTopic, withdrawDTO);
                logger.info("Money has been withdrawn from account number {} with the amount {}", withdrawDTO.getAccountNumber(), withdrawDTO.getAmount());
            } finally {
                lock.unlock();
            }
        });
    }

}