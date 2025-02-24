package com.mypayment.accountService.repo;

import com.mypayment.accountService.model.accountCreation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface accountCreationRepo extends JpaRepository<accountCreation, String> {
    Optional<accountCreation> findByAccountNumber(String accountNumber);
    Optional<accountCreation> findByAccountHolderName(String accountHolderName);
    Optional<accountCreation> findByAccountType(String accountType);
    Optional<accountCreation> findByAccountBalance(Double accountBalance);
    Optional<accountCreation> findByAccountStatus(String accountStatus);
}
