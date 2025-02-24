package com.mypayment.accountService.controller;

import com.mypayment.accountService.Constants.Accountconstants;
import com.mypayment.accountService.DTO.accountCreationDTO;
import com.mypayment.accountService.DTO.responseDTO;
import com.mypayment.accountService.exception.invalidDataException;
import com.mypayment.accountService.model.accountCreation;
import com.mypayment.accountService.service.accountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.mypayment.accountService.repo.accountCreationRepo;

@RestController
@RequestMapping("/account")
@Validated
public class accountServiceController {

    @Autowired
    private accountService accountService;

    @Autowired
    private accountCreationRepo accountCreationRepo;

    private static final Logger logger = LoggerFactory.getLogger(accountServiceController.class);

    @PostMapping("/createaccount")
    public ResponseEntity<?> createaccount(@Valid @RequestBody accountCreationDTO account) {
        String accountNumber = account.getAccountNumber();
        String accountHolderName = account.getAccountHolderName();
        String accountType = account.getAccountType();

        Optional<accountCreation> checkAccount = accountCreationRepo.findByAccountNumber(accountNumber);

        if (accountNumber.isEmpty() || accountHolderName == null || accountType.isEmpty()) {
            logger.error("Invalid data has been provided{}{}{}{}{}{}", "Account number ",account.getAccountNumber()," Name ",account.getAccountHolderName());
            throw new invalidDataException("The Account details have you have entered are not correct. Please check the account number, account holder name and the balance you have entered \n" + "Account number given is " + account.getAccountNumber() + "The name you hav given is " + account.getAccountHolderName() );
        }

        if(checkAccount.isPresent()){
            logger.error("Account with Account number {} already exists", accountNumber);
            throw new invalidDataException("The Account with Account number " + accountNumber + " already exists");
        }


        accountService.createAccount(accountNumber,accountHolderName,accountType);
        logger.info("Account has been created successfully with Account number {} and the account holder name is {}", account.getAccountNumber(), account.getAccountHolderName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new responseDTO(Accountconstants.STATUS_201, Accountconstants.MESSAGE_201, "The account has been created successfully with Account number " + account.getAccountNumber() + " and the account holder name is " + account.getAccountHolderName()));
    }

    @DeleteMapping("/deleteaccount/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@Valid @PathVariable("accountNumber") String accountNumber) {
        Optional<accountCreation> account = accountCreationRepo.findByAccountNumber(accountNumber);
        accountCreation accountCreation = account.get();

        if (account.isPresent() && accountCreation.getAccountBalance() == 0) {
            logger.info("Account has been deleted successfully with Account number {}", accountNumber);
            accountService.deleteAccount(accountNumber);
            return ResponseEntity.status(HttpStatus.OK).body(new responseDTO(Accountconstants.STATUS_200, Accountconstants.MESSAGE_DELETE_200, "The account has been deleted successfully with Account number " + accountNumber));
        }else {
            logger.error("Account with Account number {} has balance of {}", accountNumber, accountCreation.getAccountBalance());
            throw new invalidDataException("The Account with Account number " + accountNumber + " has a balance of " + accountCreation.getAccountBalance() + " and hence cannot be deleted");
        }
    }


    @GetMapping("/checkAccountExists")
    public boolean checkAccountExists(@RequestParam("accountNumber") String accountNumber) {
        return accountService.checkAccountExists(accountNumber);
    }

    @PostMapping("/checkBalance")
    public double checkBalance(@RequestParam("accountNumber") String accountNumber) {
        return accountService.checkBalance(accountNumber);
    }


}


