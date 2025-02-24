package com.mypayment.withdrawService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accountService", url = "http://localhost:8080")
public interface checkAccountpresentWithdraw{

    @GetMapping("/account/checkAccountExists")
    boolean checkAccountExistsWithdraw(@RequestParam("accountNumber") String accountNumber);
}
