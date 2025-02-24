package com.mypayment.depositService.mapper;


import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.depositService.model.deposit;

public class depositMapper {

    public static depositDTO mapToDepositDTO(deposit deposit , depositDTO depositDTO){
        depositDTO.setAccountNumber(deposit.getAccountNumber());
        depositDTO.setAmount(deposit.getAmount());
        depositDTO.setTransactionType(deposit.getTransactionType());
        depositDTO.setTransactionStatus(deposit.getTransactionStatus());
        depositDTO.setDepositTime(deposit.getDepositTime());
        return depositDTO;
    }

    public static deposit mapTOdeposit(depositDTO depositDTO, deposit deposit){
        deposit.setAccountNumber(depositDTO.getAccountNumber());
        deposit.setAmount(depositDTO.getAmount());
        deposit.setTransactionType(depositDTO.getTransactionType());
        deposit.setTransactionStatus(depositDTO.getTransactionStatus());
        deposit.setDepositTime(depositDTO.getDepositTime());
        return deposit;
    }
}
