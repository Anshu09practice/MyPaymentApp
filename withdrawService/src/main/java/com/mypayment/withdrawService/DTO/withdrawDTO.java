package com.mypayment.withdrawService.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

public class withdrawDTO implements Serializable {

    @NotNull(message = "Account number is required")
    @Size(min = 10, max = 10, message = "Account number must be 10 digits")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be a number")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    private double amount;


    private String transactionType;
    private String transactionStatus;
    private LocalDateTime withdrawDate;

    public withdrawDTO() {
    }

    public withdrawDTO(String accountNumber, double amount, String transactionType, String transactionStatus, LocalDateTime withdrawDate) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.withdrawDate = withdrawDate;
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

    public LocalDateTime getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(LocalDateTime withdrawDate) {
        this.withdrawDate = withdrawDate;
    }
}
