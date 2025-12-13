package com.bazar.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpLoginVerifyRequest {

    @NotBlank
    private String userNameOrMobile;

    @NotBlank
    @Size(min = 4, max = 10)
    private String otp;


}
