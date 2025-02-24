package com.mypayment.TransferService.controller;

import com.mypayment.TransferService.Constants.transferConstants;
import com.mypayment.TransferService.DTO.responseDTO;
import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.TransferService.client.checkAccountpresentTransfer;
import com.mypayment.TransferService.exception.invalidDataException;
import com.mypayment.TransferService.service.TransferService;
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
import com.mypayment.TransferService.client.checkBalancetransfer;

@RestController
@RequestMapping("/transferaccount")
@Validated
public class transferController {
    @Autowired
    private TransferService transferService;

    @Autowired
    private checkAccountpresentTransfer checkAccountpresent;

    @Autowired
    private checkBalancetransfer checkBalancetransfer;

    private static final Logger logger = LoggerFactory.getLogger(transferController.class);

    @PostMapping("/transfer")
    public ResponseEntity<?> transferAmount(@Valid @RequestBody transferDTO transferDTO) {
        String fromAccount = transferDTO.getFromAccount();
        String toAccount = transferDTO.getToAccount();
        boolean fromCheckid = checkAccountpresent.checkAccountExistsTransfer(fromAccount);
        boolean toCheckid = checkAccountpresent.checkAccountExistsTransfer(toAccount);
        double senderBalance = checkBalancetransfer.checkBalancetransfer(fromAccount);
        double amount = transferDTO.getAmount();

        if (!fromCheckid || !toCheckid) {
            StringBuilder errorMessage = new StringBuilder();
            if (!fromCheckid && !toCheckid) {
                errorMessage.append("The sender and receiver account numbers are invalid.");
            } else {
                if (!fromCheckid) {
                    logger.error("The account number {} does not exist", fromAccount);
                    errorMessage.append("The sender account number you have entered does not exist. Please check the account number you have entered ").append(fromAccount).append(". ");
                }
                if (!toCheckid) {
                    logger.error("The account number {} does not exist", toAccount);
                    errorMessage.append("The receiver account number you have entered does not exist. Please check the account number you have entered ").append(toAccount).append(".");
                }
            }
            throw new invalidDataException(errorMessage.toString());
        }

        if(senderBalance < amount){
            logger.error("The account number {} does not have enough balance to transfer the amount {}", fromAccount, amount);
            throw new invalidDataException("The sender does not have enough balance to transfer the amount " + amount);
        }


        if (fromAccount.isEmpty()|| toAccount.isEmpty() || amount <= 0) {
            logger.error("Invalid data has been provided please check the account number and the amount {}", amount);
            throw new invalidDataException("The Account number and the amount you have entered are not correct. Please check the account number and the amount you have entered " + "Account number given is " + fromAccount + "The amount you have entered is " + amount);
        }
        transferService.makeAtransfer(fromAccount, toAccount, amount);
        logger.info("Amount has been transferred successfully from Account number {} to Account number {} and the amount is {}", fromAccount, toAccount, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(new responseDTO(transferConstants.STATUS_201, transferConstants.MESSAGE_201, "The amount has been transferred successfully from Account number " + fromAccount + " to Account number " + toAccount + " and the amount is " + amount));
    }


}
