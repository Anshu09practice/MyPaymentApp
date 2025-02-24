package com.mypayment.depositService.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

public class depositDTO implements Serializable {

    @NotNull(message = "Account number is required")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be a number and shall be more than 10 digits")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    private double amount;


    private String transactionType;
    private String transactionStatus;
    private LocalDateTime DepositTime;

    public depositDTO() {
    }

    public depositDTO(String accountNumber, double amount, String transactionType, String transactionStatus, LocalDateTime depositTime) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.DepositTime = DepositTime;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public LocalDateTime getDepositTime() {
        return DepositTime;
    }

    public void setDepositTime(LocalDateTime depositTime) {
        DepositTime = depositTime;
    }
}
