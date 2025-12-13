package com.bazar.car.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpLoginRequest {

    @NotBlank
    private String userNameOrMobile;

}
