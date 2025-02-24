package com.mypayment.accountService.client;


import com.mypayment.depositService.DTO.depositDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "depositService", url = "http://localhost:8082")
public interface DepositServiceClient {

    @PostMapping("/depositaccount/depositInBalance")
    void deposit(@RequestBody depositDTO deposit);
}