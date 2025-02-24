package com.mypayment.accountService.service;

import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.accountService.client.DepositServiceClient;
import com.mypayment.accountService.client.WithdrawServiceClient;
import com.mypayment.accountService.exception.invalidDataException;
import com.mypayment.accountService.interfaces.accountAction;
import com.mypayment.accountService.model.accountCreation;
import com.mypayment.accountService.repo.accountCreationRepo;
import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.withdrawService.DTO.withdrawDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class accountService implements accountAction {

    @Autowired
    private DepositServiceClient depositServiceClient;

    @Autowired
    private WithdrawServiceClient withdrawServiceClient;

    @Autowired
    private KafkaTemplate<String, Object> sendAccountUpdate;

    private final ThreadPoolExecutor executorService;
    private final Lock lock = new ReentrantLock();

    private final Counter accountCounter;
    private final Counter deleteCounter;

    private final MeterRegistry meterRegistry;

    private static final String accountCreatedDeposit = "deposit-account-initiated";
    private static final String accountCreatedWithdraw = "withdraw-account-created";
    private static final String accountCreatedTransfer = "transfer-done";

    private static final Logger logger = LoggerFactory.getLogger(accountService.class);

  @Autowired
  private accountCreationRepo accountRepo;


  @Autowired
    public accountService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.accountCounter = meterRegistry.counter("accountService.account");
        this.deleteCounter = meterRegistry.counter("accountService.delete");
        this.executorService = new ThreadPoolExecutor(1,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        ExecutorServiceMetrics.monitor(meterRegistry, this.executorService, "accountService.executorService");
    }

    @Transactional
    public void createAccount(String accountNumber, String accountHolderName, String accountType) {
        accountCounter.increment();
        executorService.submit(() -> {
            lock.lock();
            try {
                logger.info("Account number is {} and the account holder name is {}", accountNumber, accountHolderName);
                Optional<accountCreation> checkAccountNumber = accountRepo.findByAccountNumber(accountNumber);
                if (checkAccountNumber.isPresent()) {
                    throw new invalidDataException("Account number already exists " + accountNumber);
                }
                accountCreation createAccount = new accountCreation(accountNumber, accountHolderName, accountType, 0.0, "ACTIVE", LocalDateTime.now());
                accountRepo.save(createAccount);
                sendAccountUpdate.send("account-created", accountNumber);
            } finally {
                lock.unlock();
            }
        });
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        deleteCounter.increment();
        lock.lock();
        try {
            logger.info("Account number is {}", accountNumber);
            Optional<accountCreation> checkAccountNumber = accountRepo.findByAccountNumber(accountNumber);
            if (checkAccountNumber.isPresent()) {
                accountCreation account = checkAccountNumber.get();
                if (account.getAccountBalance() > 0) {
                    throw new invalidDataException("Account has balance " + accountNumber);
                }
                accountRepo.deleteById(accountNumber);
                logger.info("Account number {} has been deleted", accountNumber);
            } else {
                throw new invalidDataException("Account number does not exist " + accountNumber);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    @KafkaListener(topics = "Deposit-initiated", groupId = "account-compensation-group")
    public void deposit(depositDTO depositDTO) {
        executorService.submit(() -> {
            lock.lock();
            try {
                String id = depositDTO.getAccountNumber();
                double amount = depositDTO.getAmount();
                Optional<accountCreation> checkAccountNumber = accountRepo.findByAccountNumber(id);
                if (checkAccountNumber.isPresent()) {
                    accountCreation account = checkAccountNumber.get();
                    double balance = account.getAccountBalance();
                    balance = balance + amount;
                    account.setAccountBalance(balance);
                    accountRepo.save(account);
                    depositDTO.setTransactionStatus("COMPLETED");
                    depositDTO.setDepositTime(LocalDateTime.now());
                    sendAccountUpdate.send(accountCreatedDeposit, depositDTO);
                    logger.info("Money has been deposited to account number {} with the balance {}", id, balance);
                } else if (!depositDTO.getTransactionStatus().equalsIgnoreCase("COMPLETED") || amount <= 0) {
                    accountCreation account = checkAccountNumber.get();
                    accountRepo.save(account);
                    depositDTO.setTransactionStatus("FAILED");
                    depositDTO.setDepositTime(LocalDateTime.now());
                    sendAccountUpdate.send(accountCreatedDeposit, depositDTO);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    @KafkaListener(topics = "withdraw-funds", groupId = "account-compensation-group")
    public void withdraw(withdrawDTO withdrawDTO) {
        executorService.submit(() -> {
            lock.lock();
            try {
                String id = withdrawDTO.getAccountNumber();
                double amount = withdrawDTO.getAmount();
                Optional<accountCreation> checkAccountNumber = accountRepo.findByAccountNumber(id);
                if (checkAccountNumber.isPresent()) {
                    accountCreation account = checkAccountNumber.get();
                    double balance = account.getAccountBalance();
                    balance = balance - amount;
                    account.setAccountBalance(balance);
                    accountRepo.save(account);
                    withdrawDTO.setTransactionStatus("COMPLETED");
                    withdrawDTO.setWithdrawDate(LocalDateTime.now());
                    sendAccountUpdate.send(accountCreatedWithdraw, withdrawDTO);
                    logger.info("Money has been withdrawn from account number {} with the balance {}", id, balance);
                } else if (!withdrawDTO.getTransactionStatus().equalsIgnoreCase("COMPLETED") || amount <= 0) {
                    accountCreation account = checkAccountNumber.get();
                    accountRepo.save(account);
                    withdrawDTO.setTransactionStatus("FAILED");
                    withdrawDTO.setWithdrawDate(LocalDateTime.now());
                    sendAccountUpdate.send(accountCreatedWithdraw, withdrawDTO);
                }
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    @KafkaListener(topics = "do-transfer", groupId = "account-compensation-group")
    public void transfer(transferDTO transferDTO) {
        String fromAccount = transferDTO.getFromAccount();
        String toAccount = transferDTO.getToAccount();
        double amount = transferDTO.getAmount();
        executorService.submit(() -> {
            lock.lock();
            try {
                transferDTO.setTransactionStatus("PENDING");
                withdrawDTO transferWithdrawDTO = new withdrawDTO(fromAccount, amount, "WITHDRAW", "PENDING", LocalDateTime.now());
                withdrawServiceClient.withdraw(transferWithdrawDTO);
                logger.info("Withdrawal from account {} for amount {} initiated.", fromAccount, amount);

                depositDTO transferDepositDTO = new depositDTO(toAccount, amount, "DEPOSIT", "PENDING", LocalDateTime.now());
                depositServiceClient.deposit(transferDepositDTO);
                logger.info("Deposit to account {} for amount {} initiated.", toAccount, amount);
                transferDTO.setTransactionStatus("COMPLETED");
                transferDTO.setTransactionType("TRANSFER");
                transferDTO.setTransferDate(LocalDateTime.now());
                logger.info("the transfer message is having details such as to account {} from account {} and amount {} type {} and status {}", toAccount, fromAccount, amount, transferDTO.getTransactionType(), transferDTO.getTransactionStatus());
                sendAccountUpdate.send(accountCreatedTransfer, transferDTO);
            } catch (Exception e) {
                logger.error("Error during transfer from account {} to account {} for amount {}: {}", fromAccount, toAccount, amount, e.getMessage());
                transferDTO.setTransactionStatus("FAILED");
                transferDTO.setTransferDate(LocalDateTime.now());
                sendAccountUpdate.send(accountCreatedTransfer, transferDTO);
            } finally {
                lock.unlock();
            }
        });
    }

    public boolean checkAccountExists(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber).isPresent();
    }

    public double checkBalance(String accountNumber) {
        Optional<accountCreation> account = accountRepo.findByAccountNumber(accountNumber);
        return account.map(accountCreation::getAccountBalance).orElse(0.0);
    }
}