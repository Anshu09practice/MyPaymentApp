package com.mypayment.depositService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "accountService", url = "http://localhost:8080")
public interface checkAccountpresent {

    @GetMapping("/account/checkAccountExists")
    boolean checkAccountExists(@RequestParam("accountNumber") String accountNumber);
}
