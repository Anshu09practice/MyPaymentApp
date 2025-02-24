package com.mypayment.TransferService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class TransferEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transferSerialID;

    @NotNull
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be a number")
    private String fromAccount;

    @NotNull
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be a number")
    private String toAccount;

    @NotNull
    private double amount;

    private String transactionType;
    private String transactionStatus;
    private LocalDateTime transferDate;

    public TransferEntity() {
    }

    public TransferEntity(String fromAccount, String toAccount, double amount, String transactionType, String transactionStatus, LocalDateTime transferDate) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
        this.transferDate = transferDate;
    }

    public long getTransferSerialID() {
        return transferSerialID;
    }

    public void setTransferSerialID(long transferSerialID) {
        this.transferSerialID = transferSerialID;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
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

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }
}

