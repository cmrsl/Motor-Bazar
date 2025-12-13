package com.bazar.car.util;

import java.security.SecureRandom;

public final class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    private OtpUtil() {}

    public  static String generateNumericOtp(int digits) {
        int bound = (int) Math.pow(10, digits);
        int num = random.nextInt(bound - (bound / 10)) + (bound / 10);
        return String.valueOf(num);
    }

}
