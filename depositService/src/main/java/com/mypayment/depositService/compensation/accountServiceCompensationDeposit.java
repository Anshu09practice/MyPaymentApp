package com.mypayment.depositService.compensation;


import com.mypayment.depositService.Constants.Depositconstants;
import com.mypayment.depositService.DTO.depositDTO;
import com.mypayment.depositService.DTO.responseDTO;
import com.mypayment.depositService.exception.invalidDataException;
import com.mypayment.depositService.exception.retryException;
import com.mypayment.depositService.mapper.depositMapper;
import com.mypayment.depositService.model.deposit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import com.mypayment.depositService.repo.depositRepo;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
public class accountServiceCompensationDeposit {

    @Autowired
    private depositRepo depositRepo;

    private static final Logger logger = LoggerFactory.getLogger(accountServiceCompensationDeposit.class);

    @ResponseBody
    @KafkaListener(topics= "deposit-account-initiated",groupId = "deposit-account-group")
    @Retryable(value = retryException.class,
            maxAttempts = 5 ,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void compensationPostProcessingdeposit(depositDTO compDeposit){
        if(compDeposit.getTransactionStatus().equalsIgnoreCase("COMPLETED")&& compDeposit.getTransactionType().equalsIgnoreCase("DEPOSIT")){
            deposit finalcompDeposit = depositMapper.mapTOdeposit(compDeposit,new deposit());
            depositRepo.save(finalcompDeposit);
            logger.info("Money has been deposited to account number {} with the balance {}",finalcompDeposit.getAccountNumber(),finalcompDeposit.getAmount());
        } else {
            logger.error("Transaction was not successfull");
            throw new invalidDataException("Check the account details");

        }
    }


}
