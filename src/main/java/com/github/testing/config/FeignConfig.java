package com.github.testing.config;

import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    @ConditionalOnProperty("feign.logging.enabled")
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}