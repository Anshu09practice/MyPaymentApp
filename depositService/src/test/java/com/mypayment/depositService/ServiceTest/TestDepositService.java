package com.mypayment.depositService.ServiceTest;

import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.depositService.service.DepositService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

class TestDepositService {

    @Mock
    private KafkaTemplate<String, Object> sendUpdatetoAccountService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter depositCounter;

    private DepositService depositService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock the MeterRegistry methods
        when(meterRegistry.counter("depositService.deposit")).thenReturn(depositCounter);

        // Mock the static method ExecutorServiceMetrics.monitor
        try (MockedStatic<ExecutorServiceMetrics> mocked = mockStatic(ExecutorServiceMetrics.class)) {
            mocked.when(() -> ExecutorServiceMetrics.monitor(any(MeterRegistry.class), any(ThreadPoolExecutor.class), anyString()))
                    .thenReturn(null);

            // Initialize DepositService with mocked dependencies
            depositService = new DepositService(meterRegistry);

            // Use reflection to set the private field
            Field kafkaTemplateField = DepositService.class.getDeclaredField("sendUpdatetoAccountService");
            kafkaTemplateField.setAccessible(true);
            kafkaTemplateField.set(depositService, sendUpdatetoAccountService);
        }
    }

    @Test
    void depositMoneySuccessfully() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String accountNumber = "1234567890";
        double amount = 1000.0;

        depositService.depositMoney(accountNumber, amount);

        Field executorField = DepositService.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(depositService);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(depositCounter).increment();
        verify(sendUpdatetoAccountService).send(eq("Deposit-initiated"), any(depositDTO.class));
    }

    @Test
    void depositMoneyWithZeroAmount() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String accountNumber = "1234567890";
        double amount = 0.0;

        depositService.depositMoney(accountNumber, amount);

        Field executorField = DepositService.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(depositService);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(depositCounter).increment();
        verify(sendUpdatetoAccountService).send(eq("Deposit-initiated"), any(depositDTO.class));
    }

    @Test
    void depositMoneyWithNegativeAmount() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String accountNumber = "1234567890";
        double amount = -100.0;

        depositService.depositMoney(accountNumber, amount);

        Field executorField = DepositService.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(depositService);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(depositCounter).increment();
        verify(sendUpdatetoAccountService).send(eq("Deposit-initiated"), any(depositDTO.class));
    }
}