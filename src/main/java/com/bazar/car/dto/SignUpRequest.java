package com.bazar.car.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

        @NotBlank
        @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,32}$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
        private String username;

        @NotBlank
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank
        @Pattern(regexp = "[0-9]{10}$", message = "Mobile number must be 10 digits")
        private String mobileNumber;

        @NotBlank
        @Size(min=8, message = "Password must be at least 8 character")
        private String password;
}
