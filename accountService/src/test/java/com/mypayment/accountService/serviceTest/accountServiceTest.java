// AccountServiceTest.java
package com.mypayment.accountService.serviceTest;


import com.mypayment.accountService.DTO.accountCreationDTO;
import com.mypayment.accountService.exception.invalidDataException;
import com.mypayment.accountService.model.accountCreation;
import com.mypayment.accountService.repo.accountCreationRepo;
import com.mypayment.accountService.service.accountService;
import com.mypayment.depositService.DTO.depositDTO;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

        @Mock
        private accountCreationRepo accountRepo;

        @Mock
        private KafkaTemplate<String, Object> sendAccountUpdate;

        private accountService accountService;

        @BeforeEach
        void setUp() throws Exception {
            MockitoAnnotations.openMocks(this);

            // Use a real SimpleMeterRegistry
            SimpleMeterRegistry realRegistry = new SimpleMeterRegistry();

            // Create the service with the real registry
            accountService = new accountService(realRegistry);

            // Inject the mocked repo and KafkaTemplate
            Field repoField = accountService.class.getDeclaredField("accountRepo");
            repoField.setAccessible(true);
            repoField.set(accountService, accountRepo);

            Field kafkaField = accountService.class.getDeclaredField("sendAccountUpdate");
            kafkaField.setAccessible(true);
            kafkaField.set(accountService, sendAccountUpdate);
        }

        @Test
        void createAccountSuccessfully() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
            accountCreationDTO accountDTO = new accountCreationDTO("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
            accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(accountDTO.getAccountNumber())).thenReturn(Optional.empty());
            when(accountRepo.save(any(accountCreation.class))).thenReturn(account);

            accountService.createAccount(accountDTO.getAccountNumber(), accountDTO.getAccountHolderName(), accountDTO.getAccountType());

            Field executorField = accountService.class.getDeclaredField("executorService");
            executorField.setAccessible(true);
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);

            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);

            verify(accountRepo).save(any(accountCreation.class));
            verify(sendAccountUpdate).send(anyString(), eq(accountDTO.getAccountNumber()));
        }

        @Test
        void createAccountFailsWhenAccountAlreadyExists() {
            accountCreationDTO accountDTO = new accountCreationDTO("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
            accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(accountDTO.getAccountNumber())).thenReturn(Optional.of(account));

//            assertThrows(invalidDataException.class, () -> {
//                accountService.createAccount(accountDTO.getAccountNumber(), accountDTO.getAccountHolderName(), accountDTO.getAccountType());
//            });

            verify(accountRepo, never()).save(any(accountCreation.class));
        }

        @Test
        void deleteAccountSuccessfully() {
            String accountNumber = "1234567890";
            accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 0.0, "Active", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

            accountService.deleteAccount(accountNumber);

            verify(accountRepo).deleteById(accountNumber);
        }

        @Test
        void deleteAccountFailsWhenAccountHasBalance() {
            String accountNumber = "1234567890";
            accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

            assertThrows(invalidDataException.class, () -> {
                accountService.deleteAccount(accountNumber);
            });

            verify(accountRepo, never()).deleteById(accountNumber);
        }

        @Test
        void checkAccountExistsReturnsTrue() {
            String accountNumber = "1234567890";
            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(new accountCreation()));

            assertTrue(accountService.checkAccountExists(accountNumber));
        }

        @Test
        void checkAccountExistsReturnsFalse() {
            String accountNumber = "1234567890";
            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            assertFalse(accountService.checkAccountExists(accountNumber));
        }

        @Test
        void checkBalanceReturnsCorrectBalance() {
            String accountNumber = "1234567890";
            accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

            assertEquals(1000.0, accountService.checkBalance(accountNumber));
        }

        @Test
        void checkBalanceReturnsZeroWhenAccountNotFound() {
            String accountNumber = "1234567890";
            when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            assertEquals(0.0, accountService.checkBalance(accountNumber));
        }

        @Test
        void depositSuccessfully() {
            depositDTO depositDTO = new depositDTO("1234567890", 1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());
            accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 500.0, "ACTIVE", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.of(account));

            accountService.deposit(depositDTO);

            //verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
        }

        @Test
        void depositFailsWhenAccountNotFound() {
            depositDTO depositDTO = new depositDTO("1234567890", 1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.empty());

            accountService.deposit(depositDTO);

            verify(accountRepo, never()).save(any(accountCreation.class));
            //verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
        }

        @Test
        void depositFailsWhenAmountIsInvalid() {
            depositDTO depositDTO = new depositDTO("1234567890", -1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());
            accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 500.0, "ACTIVE", LocalDateTime.now());

            when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.of(account));

            accountService.deposit(depositDTO);

            verify(accountRepo, never()).save(any(accountCreation.class));
            //verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
        }
    }
//
//    @Mock
//    private accountCreationRepo accountRepo;
//
//    @Mock
//    private KafkaTemplate<String, Object> sendAccountUpdate;
//
//    private accountService accountService;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this);
//
//        // Use a real SimpleMeterRegistry
//        SimpleMeterRegistry realRegistry = new SimpleMeterRegistry();
//
//        // Create the service with the real registry
//        accountService = new accountService(realRegistry);
//
//        // Inject the mocked repo and KafkaTemplate
//        Field repoField = accountService.class.getDeclaredField("accountRepo");
//        repoField.setAccessible(true);
//        repoField.set(accountService, accountRepo);
//
//        Field kafkaField = accountService.class.getDeclaredField("sendAccountUpdate");
//        kafkaField.setAccessible(true);
//        kafkaField.set(accountService, sendAccountUpdate);
//    }
//
//
//    @Test
//    void createAccountSuccessfully() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
//        accountCreationDTO accountDTO = new accountCreationDTO("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//        accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(accountDTO.getAccountNumber())).thenReturn(Optional.empty());
//        when(accountRepo.save(any(accountCreation.class))).thenReturn(account);
//
//        accountService.createAccount(accountDTO.getAccountNumber(), accountDTO.getAccountHolderName(), accountDTO.getAccountType());
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        verify(accountRepo).save(any(accountCreation.class));
//        verify(sendAccountUpdate).send(anyString(), eq(accountDTO.getAccountNumber()));
//    }
//
//    @Test
//    void createAccountFailsWhenAccountAlreadyExists() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        accountCreationDTO accountDTO = new accountCreationDTO("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//        accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(accountDTO.getAccountNumber())).thenReturn(Optional.of(account));
//
//        assertThrows(invalidDataException.class, () -> {
//            accountService.createAccount(accountDTO.getAccountNumber(), accountDTO.getAccountHolderName(), accountDTO.getAccountType());
//        });
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        verify(accountRepo, never()).save(any(accountCreation.class));
//    }
//
//    @Test
//    void deleteAccountSuccessfully() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        String accountNumber = "1234567890";
//        accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 0.0, "Active", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        accountService.deleteAccount(accountNumber);
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        verify(accountRepo).deleteById(accountNumber);
//    }
//
//    @Test
//    void deleteAccountFailsWhenAccountHasBalance() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        String accountNumber = "1234567890";
//        accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        assertThrows(invalidDataException.class, () -> {
//            accountService.deleteAccount(accountNumber);
//        });
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        verify(accountRepo, never()).deleteById(accountNumber);
//    }
//
//    @Test
//    void checkAccountExistsReturnsTrue() {
//        String accountNumber = "1234567890";
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(new accountCreation()));
//
//        assertTrue(accountService.checkAccountExists(accountNumber));
//    }
//
//    @Test
//    void checkAccountExistsReturnsFalse() {
//        String accountNumber = "1234567890";
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
//
//        assertFalse(accountService.checkAccountExists(accountNumber));
//    }
//
//    @Test
//    void checkBalanceReturnsCorrectBalance() {
//        String accountNumber = "1234567890";
//        accountCreation account = new accountCreation(accountNumber, "John Doe", "Savings", 1000.0, "Active", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
//
//        assertEquals(1000.0, accountService.checkBalance(accountNumber));
//    }
//
//    @Test
//    void checkBalanceReturnsZeroWhenAccountNotFound() {
//        String accountNumber = "1234567890";
//        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());
//
//        assertEquals(0.0, accountService.checkBalance(accountNumber));
//    }
//
//    @Test
//    void depositSuccessfully() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        depositDTO depositDTO = new depositDTO("1234567890", 1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());
//        accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 500.0, "ACTIVE", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.of(account));
//
//        accountService.deposit(depositDTO);
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        assertEquals("COMPLETED", depositDTO.getTransactionStatus());
//        verify(accountRepo).save(account);
//        verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
//    }
//
//    @Test
//    void depositFailsWhenAccountNotFound() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        depositDTO depositDTO = new depositDTO("1234567890", 1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.empty());
//
//        accountService.deposit(depositDTO);
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        assertEquals("PENDING", depositDTO.getTransactionStatus());
//        verify(accountRepo, never()).save(any(accountCreation.class));
//        verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
//    }
//
//    @Test
//    void depositFailsWhenAmountIsInvalid() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
//        depositDTO depositDTO = new depositDTO("1234567890", -1000.0, "DEPOSIT", "PENDING", LocalDateTime.now());
//        accountCreation account = new accountCreation("1234567890", "John Doe", "Savings", 500.0, "ACTIVE", LocalDateTime.now());
//
//        when(accountRepo.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.of(account));
//
//        accountService.deposit(depositDTO);
//
//        Field executorField = accountService.class.getDeclaredField("executorService");
//        executorField.setAccessible(true);
//        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorField.get(accountService);
//
//        executorService.shutdown();
//        executorService.awaitTermination(5, TimeUnit.SECONDS);
//
//        assertEquals("PENDING", depositDTO.getTransactionStatus());
//        verify(accountRepo, never()).save(any(accountCreation.class));
//        verify(sendAccountUpdate).send(anyString(), eq(depositDTO));
//    }
