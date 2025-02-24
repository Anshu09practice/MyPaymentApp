package com.mypayment.TransferService.mapper;

import com.mypayment.TransferService.DTO.transferDTO;
import com.mypayment.TransferService.model.TransferEntity;

public class transferDTOMapper {

    public static transferDTO maptoTransferDTO(TransferEntity transfers, transferDTO transferDTO) {
        transferDTO.setFromAccount(transfers.getFromAccount());
        transferDTO.setToAccount(transfers.getToAccount());
        transferDTO.setAmount(transfers.getAmount());
        transferDTO.setTransactionType(transfers.getTransactionType());
        transferDTO.setTransactionStatus(transfers.getTransactionStatus());
        transferDTO.setTransferDate(transfers.getTransferDate());
        return transferDTO;
    }

    public static TransferEntity maptoTransfer(transferDTO transferDTO, TransferEntity transfers) {
        transfers.setFromAccount(transferDTO.getFromAccount());
        transfers.setToAccount(transferDTO.getToAccount());
        transfers.setAmount(transferDTO.getAmount());
        transfers.setTransactionType(transferDTO.getTransactionType());
        transfers.setTransactionStatus(transferDTO.getTransactionStatus());
        transfers.setTransferDate(transferDTO.getTransferDate());
        return transfers;
    }

}
