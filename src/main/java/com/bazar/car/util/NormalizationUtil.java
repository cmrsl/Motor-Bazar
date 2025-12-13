package com.bazar.car.util;

public final class NormalizationUtil {

    private NormalizationUtil() {}

    public static String normalizeEmail(String email) {
        return  email == null ? null : email.trim().toLowerCase();
    }

    public static String normalizeUserName(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }


   public static String normalizeMobileNumber(String mobileNumber) {
        return mobileNumber == null ? null : mobileNumber.trim();
    }
}
