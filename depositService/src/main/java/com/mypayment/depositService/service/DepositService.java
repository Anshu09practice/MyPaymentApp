package com.mypayment.depositService.service;

import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.depositService.client.checkAccountpresent;
import com.mypayment.depositService.model.deposit;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.mypayment.depositService.repo.depositRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DepositService {
    @Autowired
    private KafkaTemplate<String, Object> sendUpdatetoAccountService;
    private static final String depositUpdateTopic = "Deposit-initiated";
    private static final Logger logger = LoggerFactory.getLogger(DepositService.class);
    private final Counter depositCounter;
    private final ThreadPoolExecutor executorService;
    private final Lock lock = new ReentrantLock();

    @Autowired
    private depositRepo depositRepo;

    public DepositService(MeterRegistry meterRegistry) {
        this.depositCounter = meterRegistry.counter("depositService.deposit");
        this.executorService = new ThreadPoolExecutor(1,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
                );
            ExecutorServiceMetrics.monitor(meterRegistry, this.executorService, "depositService.executorService");
    }

    @Transactional
    public void depositMoney(String accountNumber, double amount) {
        depositCounter.increment();
        executorService.submit(() -> {
            lock.lock();
            try {
                logger.info("Account number is {} and the amount deposited is {}", accountNumber, amount);
                depositDTO depositDTO = new depositDTO(accountNumber, amount, "DEPOSIT", "PENDING", LocalDateTime.now());
                sendUpdatetoAccountService.send(depositUpdateTopic, depositDTO);
                logger.info("Money has been deposited to account number {} with the amount {}", depositDTO.getAccountNumber(), depositDTO.getAmount());
            } finally {
                lock.unlock();
            }
        });
    }

}

