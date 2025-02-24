package com.mypayment.TransferService.repo;

import com.mypayment.TransferService.model.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface transferRepo extends JpaRepository<TransferEntity, Long> {
}
