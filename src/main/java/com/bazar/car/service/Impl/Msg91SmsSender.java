package com.bazar.car.service.Impl;

import com.bazar.car.config.UsersvcProperties;
import com.bazar.car.exception.ApiValidationException;
import com.bazar.car.service.SmsSender;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class Msg91SmsSender implements SmsSender {


    private final UsersvcProperties usersvcProperties;
    private final RestTemplate restTemplate;

    public Msg91SmsSender(UsersvcProperties usersvcProperties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.usersvcProperties = usersvcProperties;

    }

    @Override
    public void sendOtpSms(String mobileE164, String otp) {
        usersvcProperties.getSms().getMsg91();

        String url = "https://api.msg91.com/api/v5/flow/";
        Map<String,Object> body = new HashMap<>();
        body.put("flow_id", usersvcProperties.getSms().getMsg91().getFlowId());
        body.put("sender", usersvcProperties.getSms().getMsg91().getSenderId());
        body.put("mobiles", mobileE164);
        body.put("otp_expiry", otp);

        if (usersvcProperties.getSms().getMsg91().getDltTeId() != null && !usersvcProperties.getSms().getMsg91().getDltTeId().isBlank()) {
            body.put("DLT_TE_ID", usersvcProperties.getSms().getMsg91().getDltTeId());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authkey", usersvcProperties.getSms().getMsg91().getAuthKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            log.info("SMS sent successfully to {}: {}", mobileE164, response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to send SMS to {}: {}", mobileE164, response.getBody());
                throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to send SMS: " , response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", mobileE164, e.getMessage());
            throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR,"Failed to send SMS: " , e.getMessage());
        }

    }
}
