package com.ege.wooda.global.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import com.ege.wooda.Application;

@Configuration
@EnableFeignClients(basePackageClasses = Application.class)
public class FeignConfig {
}
