package com.bazar.car.util;

public final class EmailTemplate {

    private EmailTemplate() {}

    public static String signUpOtpSubject() {
        return "Your Verification OTP for Motor Bazaar";
    }

    public  static String signUpOtpBody(String userName, String otp, int minutesValid) {

        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial, sans-serif; background:#f4f6f8; padding:20px;">
            <div style="max-width:500px; margin:auto; background:#ffffff; padding:25px; border-radius:8px;">
                <h2 style="text-align:center; color:#1f2937;">Motor Bazaar</h2>

                <p>Hi <b>%s</b>,</p>

                <p>
                    Use the OTP below to verify your email address.
                    This OTP is valid for <b>%d minutes</b>.
                </p>

                <div style="
                    text-align:center;
                    font-size:24px;
                    font-weight:bold;
                    letter-spacing:4px;
                    background:#f3f4f6;
                    padding:15px;
                    border-radius:6px;
                    margin:20px 0;
                ">
                    %s
                </div>

                <p style="font-size:13px; color:#6b7280;">
                    If you did not request this, you can safely ignore this email.
                </p>

                <p style="font-size:13px; color:#6b7280;">
                    — Motor Bazaar Team
                </p>
            </div>
        </body>
        </html>
        """.formatted(userName, minutesValid, otp);
    }

}
