package com.mypayment.accountService.interfaces;

import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.withdrawService.DTO.withdrawDTO;
import org.springframework.stereotype.Service;

@Service
public interface accountAction {
//    public void createAccount(String accountHolderName, double balance);
//    public void deleteAccount(String accountNumber);
//    public void updateAccount(String accountNumber, double balance);
      //public String getAccount(String accountNumber);
//    public void getAllAccounts();
//    public void getAccountBalance(String accountNumber);
    public void deposit(depositDTO depositDTO);
    public void withdraw(withdrawDTO withdrawDTO);
    public void transfer(transferDTO transferDTO);
}
