package com.bazar.car.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {

    private UUID userId;
    private String username;
    private String email;
    private String status;

}
