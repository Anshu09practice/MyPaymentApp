package com.mypayment.withdrawService.controller;


import com.mypayment.withdrawService.Constants.withdrawconstants;
import com.mypayment.withdrawService.DTO.responseDTO;
import com.mypayment.withdrawService.DTO.withdrawDTO;
import com.mypayment.withdrawService.client.checkAccountpresentWithdraw;
import com.mypayment.withdrawService.client.checkBalance;
import com.mypayment.withdrawService.exception.invalidDataException;
import com.mypayment.withdrawService.service.withdrawService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/withdrawaccount")
@Validated
public class withdrawServiceController {

    @Autowired
    private withdrawService withdrawService;

    @Autowired
    private checkAccountpresentWithdraw checkAccountpresent;

    @Autowired
    private checkBalance checkBalance;

    private static final Logger logger = LoggerFactory.getLogger(withdrawServiceController.class);



    @PostMapping("/withdrawFromBalance")
    public ResponseEntity<?> withdrawFromBalance(@Valid @RequestBody withdrawDTO withdrawBalance) {
        String id = withdrawBalance.getAccountNumber();
        double amount = withdrawBalance.getAmount();
        boolean Checkid = checkAccountpresent.checkAccountExistsWithdraw(id);
        double checkBalanceAccount = checkBalance.checkBalance(id);
        if(!Checkid){
            logger.error("The account number {} does not exist", id);
            throw new invalidDataException("The account number you have entered does not exist. Please check the account number you have entered " + id);
        }

        if(checkBalanceAccount < amount){
            logger.error("The account number {} does not have enough balance to withdraw the amount {}", id, amount);
            throw new invalidDataException("The account number you have entered does not have enough balance to withdraw the amount " + amount);
        }


        if (id == null || id.isEmpty() || amount <= 0) {
            logger.error("Invalid data has been provided please check the withdraw account number {}", id);
            throw new invalidDataException("The Account number you have entered is not correct. Please check the account number you have entered " + id);
        }
        withdrawService.withdrawMoney(id, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(new responseDTO(withdrawconstants.STATUS_200, withdrawconstants.STATUS_200, "The withdraw transaction has been intiated for the amount " + amount + " and the withdraw will be made to account number " + id));
    }


}


