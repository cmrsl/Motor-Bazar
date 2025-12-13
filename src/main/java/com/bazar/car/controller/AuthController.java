package com.bazar.car.controller;

import com.bazar.car.dto.*;
import com.bazar.car.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        StopWatch stopWatch = new StopWatch("signUpEndpoint");
        stopWatch.start();
        log.info("Sign-up attempt for email: {}", signUpRequest.getEmail());
        SignUpResponse signUpResponse = userService.signUp(signUpRequest);
        stopWatch.stop();
        log.info("Sign-up completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse);
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<VerifyOtpEmailResponse> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest verifyEmailOtpRequest) {
        StopWatch stopWatch = new StopWatch("verifyEmailOtpEndpoint");
        stopWatch.start();
        log.info("Verifying email OTP for email: {}", verifyEmailOtpRequest.getUserNameOrEmail());
        VerifyOtpEmailResponse response = userService.verifyOtpEmail(verifyEmailOtpRequest);
        stopWatch.stop();
        log.info("Email OTP verification completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-email-otp")
    public ResponseEntity<ResendEmailOtpResponse> resendEmailOtp(@Valid @RequestBody ResendEmailOtpRequest resendEmailOtpRequest) {
        StopWatch stopWatch = new StopWatch("resendEmailOtpEndpoint");
        stopWatch.start();
        log.info("Resending email OTP to: {}", resendEmailOtpRequest.getUserNameOrEmail());
        ResendEmailOtpResponse response = userService.resendEmailOtp(resendEmailOtpRequest);
        stopWatch.stop();
        log.info("Resend email OTP completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login-password")
    public ResponseEntity<LoginResponse> loginWithPassword(@Valid @RequestBody PasswordLoginRequest loginRequest) {
        StopWatch stopWatch = new StopWatch("loginWithPasswordEndpoint");
        stopWatch.start();
        log.info("Password login attempt for user: {}", loginRequest.getUserNameOrMobile());
        LoginResponse loginResponse = userService.loginWithPassword(loginRequest);
        stopWatch.stop();
        log.info("Password login completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login/otp/request")
    public ResponseEntity<Void> requestLoginOtp(@Valid @RequestBody OtpLoginRequest requestLoginOtpRequest) {
        StopWatch stopWatch = new StopWatch("requestLoginOtpEndpoint");
        stopWatch.start();
        log.info("OTP login request for mobile: {}", requestLoginOtpRequest.getUserNameOrMobile());
        userService.requestLoginOtp(requestLoginOtpRequest);
        stopWatch.stop();
        log.info("OTP login request completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<LoginResponse> verifyLoginOtp(@Valid @RequestBody OtpLoginVerifyRequest verifyOtpLoginRequest) {
        StopWatch stopWatch = new StopWatch("verifyLoginOtpEndpoint");
        stopWatch.start();
        log.info("Verifying OTP login for mobile: {}", verifyOtpLoginRequest.getUserNameOrMobile());
        LoginResponse loginResponse = userService.verifyLoginOtp(verifyOtpLoginRequest);
        stopWatch.stop();
        log.info("OTP login verification completed in {} ms", stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok(loginResponse);
    }

}
