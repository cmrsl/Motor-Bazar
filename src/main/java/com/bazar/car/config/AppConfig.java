package com.bazar.car.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(UsersvcProperties.class)
public class AppConfig {
    // no method required; @EnableConfigurationProperties activates configuration properties binding

    // Bean definition for RestTemplate to resolve injection issues in Msg91SmsSender
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
