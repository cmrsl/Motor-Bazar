package com.bazar.car.service;

import com.bazar.car.entity.User;
import com.bazar.car.exception.ApiValidationException;
import com.bazar.car.util.EmailTemplate;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

//    @Value("${MAIL_FROM:motorbazaap.mp@gmail.com")
//    private String from;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpMail(String otp, User user,int otpExpiryMinutes) {
        log.info("Sending OTP email to {}", user.getEmail());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("motorbazaap.mp@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject(EmailTemplate.signUpOtpSubject());
            helper.setText(EmailTemplate.signUpOtpBody(user.getUsername(), otp, otpExpiryMinutes)
            );

            mailSender.send(message);
            log.info("Sign-up OTP email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending OTP email to {}: {}", user.getEmail(), e.getMessage());
            throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILURE", "Failed to send verification email");
        }
    }
}

