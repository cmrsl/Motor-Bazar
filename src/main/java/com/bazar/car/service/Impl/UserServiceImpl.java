package com.bazar.car.service.Impl;

import com.bazar.car.config.UsersvcProperties;
import com.bazar.car.dto.*;
import com.bazar.car.entity.*;
import com.bazar.car.exception.ApiValidationException;
import com.bazar.car.repository.OtpTokenRepository;
import com.bazar.car.repository.UserRepository;
import com.bazar.car.service.EmailService;
import com.bazar.car.service.JwtService;
import com.bazar.car.service.SmsSender;
import com.bazar.car.service.UserService;
import com.bazar.car.util.EmailTemplate;
import com.bazar.car.util.NormalizationUtil;
import com.bazar.car.util.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of UserService.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    private final UsersvcProperties usersvcProperties;
    private final SmsSender smsSender;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository,
                           OtpTokenRepository otpTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JavaMailSender javaMailSender,
                           JwtService jwtService,
                           UsersvcProperties usersvcProperties,
                           SmsSender smsSender,EmailService emailService) {
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.jwtService = jwtService;
        this.usersvcProperties = usersvcProperties;
        this.smsSender = smsSender;
        this.emailService = emailService;
    }

    private int otpDigits() {
        return usersvcProperties.getOtp().getDigits();
    }
    private int otpExpiryMinutes() {
        return usersvcProperties.getOtp().getExpiryMinutes();
    }
    private int minResendIntervalSeconds() {
        return usersvcProperties.getRateLimit().getResendMinIntervalSeconds();
    }
    private int maxSendPerHour() {
        return usersvcProperties.getRateLimit().getMaxSendsPerHour();
    }



    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normalizeAndValidateSignUpRequest");

        //Normalize and validate the sign-up request
        String username = NormalizationUtil.normalizeUserName(signUpRequest.getUsername());
        String email = NormalizationUtil.normalizeEmail(signUpRequest.getEmail());
        String mobileNumber = NormalizationUtil.normalizeMobileNumber(signUpRequest.getMobileNumber());

        stopWatch.stop();

        stopWatch.start("uniquenessCheck");
        //Check for uniqueness of username, email, and mobile number
        if (userRepository.existsByUsername(username)) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST,"DUPLICATE_USER","Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST,"DUPLICATE_EMAIL","Email already exists");
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST,"DUPLICATE_MOBILE","Mobile number already exists");
        }
        stopWatch.stop();
        stopWatch.start("createUser");
        //Create and save the new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user = userRepository.save(user);
        log.info("user created with id: {}", user.getId());
        stopWatch.stop();
        log.info("Sign-up process completed in {} ms", stopWatch.getTotalTimeMillis());
        //Return the sign-up response

        stopWatch.start("Create OTP and send");
        // Create OTP tokens and send verification messages (email/SMS)
        String otpCode = OtpUtil.generateNumericOtp(otpDigits());
        OtpToken otpToken = new OtpToken();
        otpToken.setUser(user);
        otpToken.setChannel(OtpChannel.EMAIL);
        otpToken.setPurpose(OtpPurpose.SIGN_UP_VERIFICATION);
        otpToken.setCode(otpCode);
        otpToken.setExpiresAt(Instant.now().plus(otpExpiryMinutes(), ChronoUnit.MINUTES));
        otpTokenRepository.save(otpToken);
        log.info("Create OTP and send completed for user id: {}, purpose : {}", user.getId(), otpToken.getPurpose());

        stopWatch.stop();
        stopWatch.start("sendVerificationEmail");

        // Send verification email (implementation omitted for brevity)
        emailService.sendOtpMail(otpCode,user,otpExpiryMinutes());
//        try {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(user.getEmail());
//            msg.setSubject(EmailTemplate.signUpOtpSubject());
//            msg.setText(EmailTemplate.signUpOtpBody(user.getUsername(), otpCode, otpExpiryMinutes()));
//            javaMailSender.send(msg);
//            log.info("Sign-up OTP email sent to {}", user.getEmail());
//        }catch (Exception e){
//            log.error("Failed to send sign-up OTP email to {}: {}", user.getEmail(), e.getMessage());
//            throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILURE", "Failed to send verification email");
//        }

        stopWatch.stop();
        log.info("Total sign-up process with OTP completed in {} ms", stopWatch.getTotalTimeMillis());
        return new SignUpResponse(user.getId(), user.getUsername(), user.getEmail(),user.getStatus().name());
    }

    @Override
    public VerifyOtpEmailResponse verifyOtpEmail(VerifyEmailOtpRequest req) {
         StopWatch stopWatch = new StopWatch("verifyOtpEmail");
         stopWatch.start("findUserByEmail");

         String identifier = req.getUserNameOrEmail().trim();
         User user = findUserByIdentifier(identifier).orElseThrow(() ->
                 new ApiValidationException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found for identifier: " + identifier)
         );

         if(user.isEmailVerified()){
             throw new ApiValidationException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_VERIFIED", "Email is already verified");
         }

         stopWatch.stop();
         stopWatch.start("findActiveOtpToken");
         // Find the valid OTP token for the user
         Optional<OtpToken> otpToken = otpTokenRepository.findTopByUserAndPurposeAndChannelAndConsumedAtIsNullAndExpiresAtAfterOrderByExpiresAtDesc(
                    user, OtpPurpose.SIGN_UP_VERIFICATION, OtpChannel.EMAIL, Instant.now());

         if (otpToken.isEmpty()) {
                throw new ApiValidationException(HttpStatus.BAD_REQUEST, "OTP_NOT_FOUND", "No valid OTP found or OTP has expired");
         }

         OtpToken otp = otpToken.get();
            stopWatch.stop();
            stopWatch.start("validateOtpCode");
            // Validate the OTP code
            if (!otp.getCode().equals(req.getOtp())) {
                throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_OTP", "The provided OTP code is invalid");
            }

            if (!otp.isActive()) {
                throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INACTIVE_OTP", "The provided OTP code is inactive");
            }
            stopWatch.stop();
            stopWatch.start("markOtpConsumedAndVerifyEmail");
            // Mark the OTP as consumed and verify the user's email
            otp.setConsumedAt(Instant.now());
            otpTokenRepository.save(otp);

            // Update user's email verification status
            user.setEmailVerified(true);
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            stopWatch.stop();
            log.info("Email verification completed in {} ms", stopWatch.getTotalTimeMillis());
            return new VerifyOtpEmailResponse(user.getId(),  user.getStatus().name());
    }

    @Override
    public ResendEmailOtpResponse resendEmailOtp(ResendEmailOtpRequest req) {
        StopWatch stopWatch = new StopWatch("resendEmailOtp");
        stopWatch.start("findUserByEmail");

        String identifier = req.getUserNameOrEmail().trim();
        User user = findUserByIdentifier(identifier).orElseThrow(() ->
                new ApiValidationException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found for identifier: " + identifier)
        );

        if (user.isEmailVerified()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_VERIFIED", "Email is already verified");
        }
        stopWatch.stop();
        stopWatch.start("activeOtpCountCheck");
        Instant now = Instant.now();

        Optional<OtpToken> activeOtp = otpTokenRepository.findTopByUserAndPurposeAndChannelAndConsumedAtIsNullAndExpiresAtAfterOrderByExpiresAtDesc(
                user, OtpPurpose.SIGN_UP_VERIFICATION, OtpChannel.EMAIL, now);

        // Define the rate limiting window & count
        Instant oneHourAgo = now.minus(1, ChronoUnit.HOURS);
        int sentLastHourCount = otpTokenRepository.countByUserAndPurposeAndChannelAndCreatedDateAfter(
                user, OtpPurpose.SIGN_UP_VERIFICATION, OtpChannel.EMAIL, oneHourAgo
        );

        stopWatch.stop();
        stopWatch.start("applyRateLimiting");

        if (sentLastHourCount >= maxSendPerHour()) {
            log.warn("Sending rate limit exceeded in {} ms", stopWatch.getTotalTimeMillis());
            throw new ApiValidationException(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", "Maximum OTP requests exceeded. Please try again later.");
        }

        //Idempotency check: if an active OTP exists, resend the same OTP if within min interval
        if (activeOtp.isPresent()) {
            OtpToken otpToken = activeOtp.get();
            Instant last = otpToken.getLastSentAt() != null ? otpToken.getLastSentAt() : otpToken.getCreatedDate();
            long sinceLast = Duration.between(last, Instant.now()).getSeconds();
            int remaining = Math.max(0, minResendIntervalSeconds() - (int) sinceLast);
            if (remaining > 0) {
                log.warn("Resend OTP requested too soon in {} ms", stopWatch.getTotalTimeMillis());
                throw new ApiValidationException(HttpStatus.TOO_MANY_REQUESTS, "RESEND_TOO_SOON", "Please wait " + remaining + " seconds before requesting a new OTP.");
            }

            // Resend the existing OTP
            sendVarificationEmail(user, otpToken.getCode());
            otpToken.setLastSentAt(Instant.now());
            otpToken.incrementSentCount();
            otpTokenRepository.save(otpToken);
            log.info("Resent existing OTP in {} ms", stopWatch.getTotalTimeMillis());
            return new ResendEmailOtpResponse("OTP resent successfully", 0, (int)
                    Duration.between(Instant.now(), otpToken.getExpiresAt()).toMinutes());
        }
        stopWatch.stop();
        stopWatch.start("generateAndSendNewOtp");
        // Generate and send a new OTP
        String otpCode = OtpUtil.generateNumericOtp(otpDigits());
        OtpToken newOtpToken = new OtpToken();
        newOtpToken.setUser(user);
        newOtpToken.setChannel(OtpChannel.EMAIL);
        newOtpToken.setPurpose(OtpPurpose.SIGN_UP_VERIFICATION);
        newOtpToken.setCode(otpCode);
        newOtpToken.setExpiresAt(Instant.now().plus(otpExpiryMinutes(), ChronoUnit.MINUTES));
        newOtpToken.setLastSentAt(Instant.now());
        newOtpToken.setSentCount(1);
        otpTokenRepository.save(newOtpToken);

        stopWatch.stop();
        stopWatch.start("sendVarificationEmail");
        // Send the OTP via email
        sendVarificationEmail(user, otpCode);
        stopWatch.stop();
        log.info("Generated and sent new OTP in {} ms", stopWatch.getTotalTimeMillis());
        return new ResendEmailOtpResponse("REGENERATED_AND_SENT", 0, (int)
                Duration.between(Instant.now(), newOtpToken.getExpiresAt()).toMinutes());


    }

    private void sendVarificationEmail(User user, String otpCode) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(user.getEmail());
            msg.setSubject(EmailTemplate.signUpOtpSubject());
            msg.setText(EmailTemplate.signUpOtpBody(user.getUsername(), otpCode, otpExpiryMinutes()));
            javaMailSender.send(msg);
            log.info("Sign-up OTP email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send sign-up OTP email to {}: {}", user.getEmail(), e.getMessage());
            throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILURE", "Failed to send verification email");
        }
    }


    // Helper method to find user by username or email
    private Optional<User> findUserByIdentifier(String identifier) {
        String s = identifier.trim().toLowerCase();
        if (s.contains("@")) {
            return userRepository.findByEmail(NormalizationUtil.normalizeEmail(s));
        } else {
            return userRepository.findByUsername(NormalizationUtil.normalizeEmail(s));
        }
    }

    @Override
    @Transactional
    public LoginResponse loginWithPassword(PasswordLoginRequest loginRequest) {
        StopWatch stopWatch = new StopWatch("loginWithPassword");
        stopWatch.start("findUserByIdentifier");
        String id = loginRequest.getUserNameOrMobile().trim();
        User user = (id.contains("@")
                    ? userRepository.findByEmail(id)
                    : id.matches("\\d{10,15}")
                    ? userRepository.findByMobileNumber(id)
                    : userRepository.findByUsername(id))
                    .orElseThrow( () ->
                        new ApiValidationException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found for identifier: " + id)
                    );
        stopWatch.stop();
        stopWatch.start("validatePassword");
        // Validate password
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiValidationException(HttpStatus.FORBIDDEN, "USER_INACTIVE", "User account is not active");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new ApiValidationException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username or password");
        }
        stopWatch.stop();
        stopWatch.start("generateJwtToken");
        // Generate JWT token
        String token = jwtService.issueAccessToken(
                user.getId().toString(),
                Map.of("username", user.getUsername(), "role", user.getRole().name())
        );

        stopWatch.stop();
        log.info("Login process completed in {} ms", stopWatch.getTotalTimeMillis());
        return new LoginResponse(token, jwtService.accessTokenExpiryInSeconds(),user.getUsername(), user.getRole().name());
    }

    @Override
    @Transactional
    public void requestLoginOtp(OtpLoginRequest requestLoginOtpRequest) {
        StopWatch stopWatch = new StopWatch("requestLoginOtp");
        stopWatch.start("findUserByIdentifier");
        String id = requestLoginOtpRequest.getUserNameOrMobile().trim();
        User user = (id.contains("@")
                ? userRepository.findByEmail(id)
                : id.matches("\\d{10,15}")
                ? userRepository.findByMobileNumber(id)
                : userRepository.findByUsername(id))
                .orElseThrow( () ->
                        new ApiValidationException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found for identifier: " + id)
                );
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiValidationException(HttpStatus.FORBIDDEN, "USER_INACTIVE", "User account is not active");
        }
        stopWatch.stop();
        stopWatch.start("rateLimitingCheck");
        Instant now = Instant.now();
        // Define the rate limiting window & count
        int sentLastHourCount = otpTokenRepository.countByUserAndPurposeAndChannelAndCreatedDateAfter(
                user, OtpPurpose.LOGIN_OTP, OtpChannel.SMS, now.minus(1, ChronoUnit.HOURS)
        );
        if (sentLastHourCount >= maxSendPerHour()) {
            log.warn("Sending rate limit exceeded in {} ms", stopWatch.getTotalTimeMillis());
            throw new ApiValidationException(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", "Maximum OTP requests exceeded. Please try again later.");
        }
        stopWatch.stop();
        stopWatch.start("activeToken");
        Optional<OtpToken> activeOtp = otpTokenRepository.findTopByUserAndPurposeAndChannelAndConsumedAtIsNullAndExpiresAtAfterOrderByExpiresAtDesc(
                user, OtpPurpose.LOGIN_OTP, OtpChannel.SMS, now);

        int minGap = minResendIntervalSeconds();;
        //Idempotency check: if an active OTP exists, resend the same OTP if within min interval
        if (activeOtp.isPresent()) {
            OtpToken otpToken = activeOtp.get();
            Instant last = otpToken.getLastSentAt() != null ? otpToken.getLastSentAt() : otpToken.getCreatedDate();
            long sinceLast = Duration.between(last, Instant.now()).getSeconds();
            if (sinceLast < minGap) {
                int remaining = (int) (minGap - sinceLast);
                log.info("Sending rate limit exceeded in {} ms", remaining);
                return;
            }
            // Resend the existing OTP
            smsSender.sendOtpSms(user.getMobileNumber(), otpToken.getCode());
            otpToken.setLastSentAt(Instant.now());
            otpToken.incrementSentCount();
            otpTokenRepository.save(otpToken);
            log.info("Resent existing OTP in {} ms", stopWatch.getTotalTimeMillis());
            return;
        }
        stopWatch.stop();
        stopWatch.start("generateAndSendNewOtp");
        // Generate and send a new OTP
        String otpCode = OtpUtil.generateNumericOtp(otpDigits());
        OtpToken newOtpToken = new OtpToken();
        newOtpToken.setUser(user);
        newOtpToken.setChannel(OtpChannel.SMS);
        newOtpToken.setPurpose(OtpPurpose.LOGIN_OTP);
        newOtpToken.setCode(otpCode);
        newOtpToken.setExpiresAt(Instant.now().plus(otpExpiryMinutes(), ChronoUnit.MINUTES));
        newOtpToken.setLastSentAt(Instant.now());
        newOtpToken.setSentCount(1);
        otpTokenRepository.save(newOtpToken);
        // Send the OTP via SMS
        smsSender.sendOtpSms(user.getMobileNumber(), otpCode);
        stopWatch.stop();
        log.info("Generated and sent new OTP in {} ms", stopWatch.getTotalTimeMillis());


    }

    @Override
    @Transactional
    public LoginResponse verifyLoginOtp(OtpLoginVerifyRequest verifyOtpLoginRequest) {
        StopWatch stopWatch = new StopWatch("verifyLoginOtp");
        stopWatch.start("findUserByIdentifier");
        String id = verifyOtpLoginRequest.getUserNameOrMobile().trim();
        User user = (id.contains("@")
                ? userRepository.findByEmail(id)
                : id.matches("\\d{10,15}")
                ? userRepository.findByMobileNumber(id)
                : userRepository.findByUsername(id))
                .orElseThrow( () ->
                        new ApiValidationException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found for identifier: " + id)
                );
        stopWatch.stop();
        stopWatch.start("findActiveOtpToken");
        // Find the valid OTP token for the user
        OtpToken otpToken = otpTokenRepository.findTopByUserAndPurposeAndChannelAndConsumedAtIsNullAndExpiresAtAfterOrderByExpiresAtDesc(
                user, OtpPurpose.LOGIN_OTP, OtpChannel.SMS, Instant.now())
                .orElseThrow(() ->
                        new ApiValidationException(HttpStatus.BAD_REQUEST, "OTP_NOT_FOUND", "No valid OTP found or OTP has expired")
                );
        stopWatch.stop();
        stopWatch.start("validateOtpCode");
        // Validate the OTP code
        if (!otpToken.getCode().equals(verifyOtpLoginRequest.getOtp())) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_OTP", "The provided OTP code is invalid");
        }
        stopWatch.stop();
        stopWatch.start("markOtpConsumedAndGenerateToken");
        // Mark the OTP as consumed
        otpToken.setConsumedAt(Instant.now());
        otpTokenRepository.save(otpToken);
        stopWatch.stop();
        stopWatch.start("generateJwtToken");
        // Generate JWT token
        String token = jwtService.issueAccessToken(
                user.getId().toString(),
                Map.of("username", user.getUsername(), "role", user.getRole().name())
        );
        stopWatch.stop();
        log.info("Login via OTP process completed in {} ms", stopWatch.getTotalTimeMillis());
        return new LoginResponse(token, jwtService.accessTokenExpiryInSeconds(),user.getUsername(), user.getRole().name());
    }

}
