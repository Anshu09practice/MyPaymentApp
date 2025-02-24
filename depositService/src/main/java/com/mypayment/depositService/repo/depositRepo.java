package com.mypayment.depositService.repo;



import com.mypayment.depositService.model.deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface depositRepo extends JpaRepository<deposit, Long> {
    String findByAccountNumber(String accountNumber);
}
