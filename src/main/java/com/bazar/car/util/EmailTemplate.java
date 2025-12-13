package com.bazar.car.util;

public final class EmailTemplate {

    private EmailTemplate() {}

    public static String signUpOtpSubject() {
        return "Your Verification OTP for Motor Bazaar";
    }

    public  static String signUpOtpBody(String userName, String otp, int minutesValid) {
        return "Hi "+ userName+ "\n\n" +
                "Thank you for signing up with Motor Bazaar! " +
                "Your One-Time Password (OTP) " + otp + " is required to " +
                "verify your account and complete the registration process." +
                "This OTP is valid for the next "+minutesValid+" minutes. " +
                "Please do not share it with anyone. \n\n" +
                "If you didn't request this OTP, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Motor Bazaar Team";
    }

//    public static String getOtpEmailTemplate(String otp) {
//        return "<html>" +
//                "<body>" +
//                "<h2>Your One-Time Password (OTP)</h2>" +
//                "<p>Use the following OTP to complete your action:</p>" +
//                "<h3 style=\"color:blue;\">" + otp + "</h3>" +
//                "<p>This OTP is valid for the next 10 minutes. Please do not share it with anyone.</p>" +
//                "<br>" +
//                "<p>Thank you,<br/>Bazar Car Team</p>" +
//                "</body>" +
//                "</html>";
//    }


}
