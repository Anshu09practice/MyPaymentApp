package com.mypayment.TransferService.ServiveTest;

import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.TransferService.service.TransferService;
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

class TransferServiceTest {

    @Mock
    private KafkaTemplate<String, Object> TransferkafkaTemplate;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter TransferCounter;

    private TransferService transferService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock the MeterRegistry methods
        when(meterRegistry.counter("TransferService.Transfer")).thenReturn(TransferCounter);

        // Mock the static method ExecutorServiceMetrics.monitor
        try (MockedStatic<ExecutorServiceMetrics> mocked = mockStatic(ExecutorServiceMetrics.class)) {
            mocked.when(() -> ExecutorServiceMetrics.monitor(any(MeterRegistry.class), any(ThreadPoolExecutor.class), anyString()))
                    .thenReturn(null);

            // Initialize TransferService with mocked dependencies
            transferService = new TransferService(meterRegistry);

            // Use reflection to set the private field
            Field kafkaTemplateField = TransferService.class.getDeclaredField("TransferkafkaTemplate");
            kafkaTemplateField.setAccessible(true);
            kafkaTemplateField.set(transferService, TransferkafkaTemplate);
        }
    }

    @Test
    void makesTransferSuccessfully() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String fromAccount = "1111111111";
        String toAccount = "2222222222";
        double amount = 500.0;

        transferService.makeAtransfer(fromAccount, toAccount, amount);

        Field executorField = TransferService.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(transferService);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(TransferCounter).increment();
        verify(TransferkafkaTemplate).send(eq("do-transfer"), any(transferDTO.class));
    }

    @Test
    void makesTransferWithZeroAmount() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String fromAccount = "1111111111";
        String toAccount = "2222222222";
        double amount = 0.0;

        transferService.makeAtransfer(fromAccount, toAccount, amount);

        Field executorField = TransferService.class.getDeclaredField("executorService");
        executorField.setAccessible(true);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(transferService);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        verify(TransferCounter).increment();
        verify(TransferkafkaTemplate).send(eq("do-transfer"), any(transferDTO.class));
    }
}