package com.bazar.car.config;


import com.cloudinary.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * HTTP security for the application.
 *  - Permits public endpoints (signup, login, verify-otp, resend-otp).
 *  - Secures all other endpoints (requires authentication).
 *  - Disables CSRF protection for simplicity (not recommended for production).
 *  - Enables CORS to allow cross-origin requests.
 */


@Configuration
@Slf4j
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .cors().and().csrf().disable()
//            .authorizeHttpRequests().anyRequest().permitAll()
//            ;
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        log.info("Configuring HTTP security settings : permit api/auth/** endpoints, secure others");
        http
                //Typically REST setup: disable CSRF (we are stateless; no sessions-backed forms)
                .csrf(csrf -> csrf.disable())

                // CORS (adjust allowed origins, methods, headers as per your client domains)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("*"));
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
                    cfg.setExposedHeaders(List.of("X-Request-ID"));
                    cfg.setAllowCredentials(false);
                    cfg.setMaxAge(3600L);
                    return cfg;
                }))

                // Authorization requests explicitly defined
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/sign-up").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/verify-email-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/resend-email-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login/otp/request").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login/otp/verify").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Health/actuator (Optional): uncomment if you want to expose health endpoints
                        //.requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // Everything else requires authentication
                        .anyRequest().denyAll()
                )

                // No HTTP Basic or form login for APIs; use token-based auth (e.g., JWT) in real apps
                .httpBasic(Customizer.withDefaults())
                .formLogin( form -> form.disable());
        return http.build();
    }




}
