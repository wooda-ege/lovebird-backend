package com.ege.wooda.global.config.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Retryer;
import feign.Retryer.Default;

@Configuration
public class FeignRetryConfig {
    @Bean
    public Retryer retryer() {
        return new Default(1000, 1500, 1);
    }
}
