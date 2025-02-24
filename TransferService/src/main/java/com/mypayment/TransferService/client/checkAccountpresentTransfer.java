package com.mypayment.TransferService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accountService", url = "http://localhost:8080")
public interface checkAccountpresentTransfer{

    @GetMapping("/account/checkAccountExists")
    boolean checkAccountExistsTransfer(@RequestParam("accountNumber") String accountNumber);
}
