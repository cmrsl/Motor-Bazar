package com.bazar.car.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otp_tokens",
               indexes = {
                  @Index(name = "idx_otp_user_purpose_active", columnList = "user_id, purpose, consumed_at, expires_at"),
               })
@Getter
@Setter
public class OtpToken extends Auditable{

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_otp_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private OtpChannel channel = OtpChannel.EMAIL;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 32)
    private OtpPurpose purpose = OtpPurpose.SIGN_UP_VERIFICATION;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name ="attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name ="last_sent_at")
    private Instant lastSentAt; //updated on initial send and resend

    @Column(name ="sent_count")
    private Integer sentCount;

    //helpers
    public boolean isActive(){
        return consumedAt == null && Instant.now().isBefore(expiresAt);
    }

    public int getSafeSentCount(){
        return sentCount == null ? 0 : sentCount;
    }

    public void incrementSentCount(){
        this.sentCount = getSafeSentCount() + 1;
    }
}
