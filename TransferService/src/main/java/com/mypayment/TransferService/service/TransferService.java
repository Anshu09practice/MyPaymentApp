package com.mypayment.TransferService.service;

import com.mypayment.TransferService.DTO.transferDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.mypayment.TransferService.repo.transferRepo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TransferService {
    @Autowired
    private transferRepo transferRepo;

    @Autowired
    private KafkaTemplate<String, Object> TransferkafkaTemplate;

    private static final String transferUpdateTopic = "do-transfer";

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final Counter TransferCounter;
    private final ThreadPoolExecutor executorService;
    private final Lock lock = new ReentrantLock();

    public TransferService(MeterRegistry meterRegistry) {
        this.TransferCounter = meterRegistry.counter("TransferService.Transfer");
        this.executorService = new ThreadPoolExecutor(1,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        ExecutorServiceMetrics.monitor(meterRegistry, this.executorService, "TransferService.executorService");
    }



    @Transactional
    public void makeAtransfer(String fromAccount, String toAccount, double amount){
        TransferCounter.increment();
        executorService.submit(() -> {
            lock.lock();
            try {
                logger.info("The transfer is from account {} to account {} and the amount is {}",fromAccount,toAccount,amount);
                transferDTO transfer = new transferDTO(fromAccount,toAccount,amount,"PENDING","TRANSFER", LocalDateTime.now());
                TransferkafkaTemplate.send(transferUpdateTopic,transfer);
            } finally {
                lock.unlock();
            }
        });
    }
}
