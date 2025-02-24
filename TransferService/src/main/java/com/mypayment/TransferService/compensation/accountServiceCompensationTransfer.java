package com.mypayment.TransferService.compensation;

import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.TransferService.exception.invalidDataException;
import com.mypayment.TransferService.mapper.transferDTOMapper;
import com.mypayment.TransferService.model.TransferEntity;
import com.mypayment.TransferService.repo.transferRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class accountServiceCompensationTransfer {

    @Autowired
    private transferRepo transferRepo;

    private static final Logger logger = LoggerFactory.getLogger(accountServiceCompensationTransfer.class);

    @KafkaListener(topics = {"transfer-done"},groupId = "transfer-account-group")
    public void compensationPostProcessingtransfer(transferDTO compTransfer){

        if(compTransfer.getTransactionStatus().equalsIgnoreCase("COMPLETED") || compTransfer.getTransactionType()
                .equalsIgnoreCase("TRANSFER")){
            TransferEntity finalcompTransfer = transferDTOMapper.maptoTransfer(compTransfer,new TransferEntity());
            transferRepo.save(finalcompTransfer);
            logger.info("Money has been transferred to account number {} with the balance {}",finalcompTransfer.getToAccount(),finalcompTransfer.getAmount());

        } else if(compTransfer.getTransactionStatus().equalsIgnoreCase("FAILED")){
            logger.error("Transaction was not successfully implemented");
            throw new invalidDataException("Check the account details");

        }
    }

}
