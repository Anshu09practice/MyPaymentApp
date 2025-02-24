package com.mypayment.withdrawService.repo;




import com.mypayment.withdrawService.model.withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface withdrawRepo extends JpaRepository<withdraw, Long> {
}
