package com.bazar.car.service;

import com.bazar.car.dto.*;

public interface UserService {

    SignUpResponse signUp(SignUpRequest signUpRequest);

    VerifyOtpEmailResponse verifyOtpEmail(VerifyEmailOtpRequest verifyEmailOtpRequest);

    ResendEmailOtpResponse resendEmailOtp(ResendEmailOtpRequest resendEmailOtpRequest);

    LoginResponse loginWithPassword(PasswordLoginRequest loginRequest);

    void requestLoginOtp(OtpLoginRequest requestLoginOtpRequest);

    LoginResponse verifyLoginOtp(OtpLoginVerifyRequest verifyOtpLoginRequest);

}
