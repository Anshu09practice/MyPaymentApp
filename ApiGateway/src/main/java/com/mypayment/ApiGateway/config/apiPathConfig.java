package com.mypayment.ApiGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class apiPathConfig {

    @Bean
    public RouteLocator myPayment(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/mypayment/account/**")
                        .filters(f -> f.rewritePath("/mypayment/account/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("mypayment-account-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/accountService")))
                        .uri("lb://ACCOUNTSERVICE"))
                .route(p -> p
                        .path("/mypayment/deposit/**")
                        .filters(f -> f.rewritePath("/mypayment/deposit/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("mypayment-deposit-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/depositService")))
                        .uri("lb://DEPOSITSERVICE"))
                .route(p -> p
                        .path("/mypayment/withdraw/**")
                        .filters(f -> f.rewritePath("/mypayment/withdraw/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("mypayment-withdraw-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/withdrawService")))
                        .uri("lb://WITHDRAWSERVICE"))
                .route(p -> p
                        .path("/mypayment/transfer/**")
                        .filters(f -> f.rewritePath("/mypayment/transfer/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("mypayment-transfer-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/transferService")))
                        .uri("lb://TRANSFERSERVICE"))
                .build();
    }
}