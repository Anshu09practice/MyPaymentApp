package com.mypayment.withdrawService.mapper;

import com.mypayment.withdrawService.DTO.withdrawDTO;
import com.mypayment.withdrawService.model.withdraw;

public class withdrawMapper {

    public static withdrawDTO mapTowithdrawDTO(withdraw withdraw , withdrawDTO withdrawDTO){
        withdrawDTO.setAccountNumber(withdraw.getAccountNumber());
        withdrawDTO.setAmount(withdraw.getAmount());
        withdrawDTO.setTransactionType(withdraw.getTransactionType());
        withdrawDTO.setTransactionStatus(withdraw.getTransactionStatus());
        withdrawDTO.setWithdrawDate(withdraw.getWithdrawDate());
        return withdrawDTO;
    }

    public static withdraw mapTOwithdraw(withdrawDTO withdrawDTO, withdraw withdraw){
        withdraw.setAccountNumber(withdrawDTO.getAccountNumber());
        withdraw.setAmount(withdrawDTO.getAmount());
        withdraw.setTransactionType(withdrawDTO.getTransactionType());
        withdraw.setTransactionStatus(withdrawDTO.getTransactionStatus());
        withdraw.setWithdrawDate(withdrawDTO.getWithdrawDate());
        return withdraw;
    }
}
