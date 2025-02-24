package com.mypayment.withdrawService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class withdraw implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long withdrawSerialID;
    private String accountNumber;
    private double amount;
    private String transactionType;
    private String transactionStatus;
    private LocalDateTime withdrawDate;

    public withdraw(){}

    public withdraw(String accountNumber, double amount, String transactionType, String transactionStatus, LocalDateTime withdrawDate) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.withdrawDate = withdrawDate;
    }

    public long getWithdrawSerialID() {
        return withdrawSerialID;
    }

    public void setWithdrawSerialID(long withdrawSerialID) {
        this.withdrawSerialID = withdrawSerialID;
    }

    public String  getAccountNumber() {
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
