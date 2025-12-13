package com.bazar.car.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;


@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_mobile", columnNames = "mobile_number")
       })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 32)
    @Column(name = "username", nullable = false, length = 64)
    private  String username;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 32)
    private String email;

    @NotBlank
    @Column(name = "mobile_number", nullable = false, length = 32)
    private String mobileNumber;//store normalized E.164 later (+countryCode number)

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "mobile_verified", nullable = false)
    private boolean mobileVerified = false;

    @Size(min = 12, max = 12)
    @Column(name = "aadhaar_number", nullable = true, length = 64)
    private String aadhaarNumber;


    @Size(min = 10, max = 10)
    @Column(name = "pan_number", nullable = true, length = 64)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name ="role", length = 32)
    private Role role= Role.CUSTOMER;

    @PrePersist
    @PreUpdate
    public void normalize() {
        if (username != null) username = username.trim().toLowerCase();
        if (email != null) email = email.trim().toLowerCase();
        if (mobileNumber != null) mobileNumber = mobileNumber.trim();
        if (role == null) role = Role.CUSTOMER;
    }

}
