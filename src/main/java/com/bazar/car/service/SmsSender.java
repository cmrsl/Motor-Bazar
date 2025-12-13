package com.bazar.car.service;

public interface SmsSender {
    void sendOtpSms(String mobileE164, String otp);
}
