package com.mypayment.withdrawService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.mypayment.withdrawService.repo")
public class withdrawJPARepo {
}
