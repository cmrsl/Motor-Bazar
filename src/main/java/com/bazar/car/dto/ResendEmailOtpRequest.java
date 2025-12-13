package com.bazar.car.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendEmailOtpRequest {

    @NotBlank(message = "Username or mobile number must not be blank")
    private String userNameOrEmail;

}
