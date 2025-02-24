package com.mypayment.accountService.mapper;

import com.mypayment.accountService.DTO.accountCreationDTO;
import com.mypayment.accountService.model.accountCreation;

public class accountCreationMapper {

    public static accountCreationDTO maptoAccountCreationDTO(accountCreation accountCreation, accountCreationDTO accountCreationDTO){
        accountCreationDTO.setAccountNumber(accountCreation.getAccountNumber());
        accountCreationDTO.setAccountHolderName(accountCreation.getAccountHolderName());
        accountCreationDTO.setAccountType(accountCreation.getAccountType());
        accountCreationDTO.setAccountBalance(accountCreation.getAccountBalance());
        accountCreationDTO.setAccountStatus(accountCreation.getAccountStatus());
        accountCreationDTO.setCreatedDate(accountCreation.getCreatedDate());
        return accountCreationDTO;
    }

    public static accountCreation maptoAccountCreation(accountCreationDTO accountCreationDTO, accountCreation accountCreation){
        accountCreation.setAccountNumber(accountCreationDTO.getAccountNumber());
        accountCreation.setAccountHolderName(accountCreationDTO.getAccountHolderName());
        accountCreation.setAccountType(accountCreationDTO.getAccountType());
        accountCreation.setAccountBalance(accountCreationDTO.getAccountBalance());
        accountCreation.setAccountStatus(accountCreationDTO.getAccountStatus());
        accountCreation.setCreatedDate(accountCreationDTO.getCreatedDate());
        return accountCreation;
    }
}
