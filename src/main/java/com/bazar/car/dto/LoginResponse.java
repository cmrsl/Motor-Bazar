package com.bazar.car.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String tokenType = "Bearer";
    private String accessToken;
    private long expiresIn;
    private String username;
    private String role;

    public LoginResponse(String accessToken, long expiresIn, String username, String role) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
    }

}
