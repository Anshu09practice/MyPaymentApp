package com.mypayment.TransferService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accountService", url = "http://localhost:8080")
public interface checkBalancetransfer {
    @PostMapping("/account/checkBalance")
    double checkBalancetransfer(@RequestParam("accountNumber") String accountNumber);
}
