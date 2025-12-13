package com.bazar.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailOtpRequest {

    @NotBlank(message = "Username or Email cannot be blank")
    private String userNameOrEmail;

    @NotBlank(message = "OTP cannot be blank")
    @Size(min = 4, max = 10, message = "OTP must be between 4 and 10 characters")
    private String otp;
}
