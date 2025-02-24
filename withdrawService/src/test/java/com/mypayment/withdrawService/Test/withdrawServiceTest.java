package com.mypayment.withdrawService.Test;

import com.mypayment.withdrawService.DTO.withdrawDTO;
import com.mypayment.withdrawService.service.withdrawService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.util.concurrent.*;

import static org.mockito.Mockito.*;

class withdrawServiceTest {

    @Mock
    private KafkaTemplate<String, Object> sendUpdatetoAccountServicewithdraw;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter withdrawCounter;

    private withdrawService withdrawService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock the MeterRegistry methods
        when(meterRegistry.counter("withdrawService.withdraw")).thenReturn(withdrawCounter);

        // Mock the static method ExecutorServiceMetrics.monitor
        try (MockedStatic<ExecutorServiceMetrics> mocked = mockStatic(ExecutorServiceMetrics.class)) {
            mocked.when(() -> ExecutorServiceMetrics.monitor(any(MeterRegistry.class), any(ThreadPoolExecutor.class), anyString()))
                    .thenReturn(null);

            // Initialize withdrawService with mocked dependencies
            withdrawService = new withdrawService(meterRegistry);

            // Use reflection to set the private field
            Field kafkaTemplateField = withdrawService.class.getDeclaredField("sendUpdatetoAccountServicewithdraw");
            kafkaTemplateField.setAccessible(true);
            kafkaTemplateField.set(withdrawService, sendUpdatetoAccountServicewithdraw);
        }
    }

    @Test
    void testWithdrawMoney() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String accountNumber = "1234567890";
        double amount = 100.0;

        withdrawService.withdrawMoney(accountNumber, amount);

        // Use reflection to access the private executorServicewithdraw field
        Field executorServiceField = withdrawService.class.getDeclaredField("executorServicewithdraw");
        executorServiceField.setAccessible(true);
        ThreadPoolExecutor executorServicewithdraw = (ThreadPoolExecutor) executorServiceField.get(withdrawService);

        // Wait for the asynchronous operation to complete
        executorServicewithdraw.shutdown();
        executorServicewithdraw.awaitTermination(5, TimeUnit.SECONDS);

        verify(withdrawCounter).increment();
        verify(sendUpdatetoAccountServicewithdraw).send(eq("withdraw-funds"), any(withdrawDTO.class));
    }
}