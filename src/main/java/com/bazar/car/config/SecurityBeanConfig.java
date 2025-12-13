package com.bazar.car.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Defines security-related beans for the application.
 */
@Configuration
public class SecurityBeanConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BcryptPasswordEncoder is a strong hashing function for encoding passwords
        return new BCryptPasswordEncoder();
    }


}
