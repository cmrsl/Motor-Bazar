package com.bazar.car.repository;

import com.bazar.car.entity.OtpChannel;
import com.bazar.car.entity.OtpPurpose;
import com.bazar.car.entity.OtpToken;
import com.bazar.car.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {

    Optional<OtpToken> findTopByUserAndPurposeAndChannelAndConsumedAtIsNullAndExpiresAtAfterOrderByExpiresAtDesc(
            User user, OtpPurpose purpose, OtpChannel channel, Instant now
    );

    Optional<OtpToken> findTopByUserAndPurposeAndChannelOrderByCreatedDateDesc(
            User user, OtpPurpose purpose, OtpChannel channel
    );

    int countByUserAndPurposeAndChannelAndCreatedDateAfter(
            User user, OtpPurpose purpose, OtpChannel channel, Instant after
    );

    List<OtpToken> findByUserAndPurposeOrderByExpiresAtDesc(
            User user, OtpPurpose purpose
    );

}
