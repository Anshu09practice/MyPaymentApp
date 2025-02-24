package com.mypayment.withdrawService.compensation;


import com.mypayment.withdrawService.DTO.withdrawDTO;
import com.mypayment.withdrawService.exception.invalidDataException;
import com.mypayment.withdrawService.mapper.withdrawMapper;
import com.mypayment.withdrawService.model.withdraw;
import com.mypayment.withdrawService.repo.withdrawRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class accountServiceCompensationwithdraw {

    @Autowired
    private withdrawRepo withdrawRepo;

    private static final Logger logger = LoggerFactory.getLogger(accountServiceCompensationwithdraw.class);

    @KafkaListener(topics= "withdraw-account-created",groupId = "withdraw-account-group")
    public void compensationPostProcessingwithdraw(withdrawDTO compWithdraw){
        if(compWithdraw.getTransactionStatus().equalsIgnoreCase("COMPLETED")&& compWithdraw.getTransactionType().equalsIgnoreCase("WITHDRAW")){
            withdraw finalcompWithdraw = withdrawMapper.mapTOwithdraw(compWithdraw,new withdraw());
            withdrawRepo.save(finalcompWithdraw);
            logger.info("Money has been withdrawn to account number {} with the balance {}",finalcompWithdraw.getAccountNumber(),finalcompWithdraw.getAmount());

        } else {
            logger.error("Transaction was not successfully implemented");
            throw new invalidDataException("Check the account details");

        }
    }


}
