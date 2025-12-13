package com.bazar.car.service;

import com.bazar.car.config.UsersvcProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    private final UsersvcProperties cfg;

    public JwtService(UsersvcProperties usersvcProperties) {
        this.cfg = usersvcProperties;
    }

    public String issueAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(cfg.getJwt().getAccessExpMinutes() * 60L);
        return Jwts.builder()
                .setIssuer(cfg.getJwt().getIssuer())
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(cfg.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

        // Implementation for issuing JWT access token
    public long accessTokenExpiryInSeconds() {
        return cfg.getJwt().getAccessExpMinutes() * 60L;
    }


}
