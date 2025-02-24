package com.mypayment.accountService.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.time.LocalDateTime;

public class accountCreationDTO implements Serializable {


    @NotNull(message = "Account number is required")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Check the account number it is not valid")
    private String accountNumber;

    @NotNull(message = "Account holder name is required")
    @Size(min = 3, max = 50, message = "Account holder name must be between 3 and 50 characters")
    private String accountHolderName;

    @NotNull(message = "Account type is required")
    @Size(min = 3, max = 50, message = "Account type must be between 3 and 50 characters")
    private String accountType;

    private double accountBalance;
    private String accountStatus;

    private LocalDateTime createdDate;

    public accountCreationDTO() {
    }

    public accountCreationDTO(String accountNumber, String accountHolderName, String accountType, double accountBalance, String accountStatus, LocalDateTime createdDate) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
        this.accountStatus = accountStatus;
        this.createdDate = createdDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
