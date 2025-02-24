package com.mypayment.depositService.controller;


import com.mypayment.depositService.Constants.Depositconstants;
import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.depositService.DTO.responseDTO;
import com.mypayment.depositService.exception.invalidDataException;
import com.mypayment.depositService.service.DepositService;
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

import java.util.Optional;
import com.mypayment.depositService.client.checkAccountpresent;


@RestController
@RequestMapping("/depositaccount")
@Validated
public class depositServiceController{

    @Autowired
    private DepositService depositService;

    @Autowired
    private checkAccountpresent checkAccountpresent;

    private static final Logger logger = LoggerFactory.getLogger(depositServiceController.class);

    @PostMapping("/depositInBalance")
    public ResponseEntity<?> depositInBalance(@Valid @RequestBody depositDTO depositBalance) {
        String id = depositBalance.getAccountNumber();
        double amount = depositBalance.getAmount();
        boolean checkID = checkAccountpresent.checkAccountExists(id);
        if(!checkID) {
            logger.error("The account number {} does not exist", id);
            throw new invalidDataException("The account number you have entered does not exist. Please check the account number you have entered " + id);
        }

        if(id == null || id.isEmpty() || amount <= 0) {
            logger.error("Invalid data has been provided please check the deposit account number {} and the deposit amount {}", id, amount);
            throw new invalidDataException("The account number or the amount you have entered are not correct. Please check the account number or the amount you have entered " + id + " " + amount);
        }
        depositService.depositMoney(id, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(new responseDTO(Depositconstants.STATUS_200, Depositconstants.STATUS_200, "The deposit transaction has been intiated for the amount " + amount + " and the deposit will be made to account number " + id));
    }


}


