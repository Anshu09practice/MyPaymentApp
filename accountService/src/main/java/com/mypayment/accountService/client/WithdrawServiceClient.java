package com.mypayment.accountService.client;


import com.mypayment.withdrawService.DTO.withdrawDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "withdrawService", url = "http://localhost:8083")
public interface WithdrawServiceClient {

    @PostMapping("/withdrawaccount/withdrawFromBalance")
    void withdraw(@RequestBody withdrawDTO withdraw);
}