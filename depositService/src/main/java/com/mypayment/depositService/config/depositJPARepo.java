package com.mypayment.depositService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.mypayment.depositService.repo")
public class depositJPARepo {
}
