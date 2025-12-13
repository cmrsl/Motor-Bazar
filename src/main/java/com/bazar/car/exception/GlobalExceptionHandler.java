package com.bazar.car.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Map<String, Object>> handleApiValidationException(ApiValidationException ex) {
        log.error("API Validation Error: {}", ex.getDescription());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getError());
        body.put("description", ex.getDescription());
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, ex.getStatus());
    }



    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVehicleNotFound(VehicleNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("timestamp", Instant.now());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Server Error");
        body.put("description", "Something went wrong");
        body.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
