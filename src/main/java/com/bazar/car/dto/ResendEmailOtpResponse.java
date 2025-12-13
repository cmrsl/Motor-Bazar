package com.bazar.car.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendEmailOtpResponse {

    private String outcome;
    private Integer waitSeconds;
    private Integer expiresInSeconds;

}
