package com.mypayment.ApiGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class fallBackController {

    @PostMapping("/accountService")
    public Mono<String> accountServiceFallBackMethod() {
        return Mono.just( "Account Service is taking longer than expected." +
                " Please try again later");
    }

    @PostMapping("/depositService")
    public Mono<String> depositServiceFallBackMethod() {
        return Mono.just("Deposit Service is taking longer than expected." +
                " Please try again later");
    }

    @PostMapping("/withdrawService")
    public Mono<String> withdrawServiceFallBackMethod() {
        return Mono.just("Withdraw Service is taking longer than expected." +
                " Please try again later");
    }

    @PostMapping("/transferService")
    public Mono<String> transferServiceFallBackMethod() {
        return Mono.just( "Transfer Service is taking longer than expected." +
                " Please try again later");
    }
}
