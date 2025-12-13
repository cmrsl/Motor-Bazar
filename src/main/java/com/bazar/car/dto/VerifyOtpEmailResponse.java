package com.bazar.car.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpEmailResponse {

    private UUID userId;
    private String status;
}
