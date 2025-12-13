package com.bazar.car.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


public record VehicleResponseDto(
        Long id,
        String brand,
        String model,
        Integer year,
        String color,
        String registrationNumber,
        Integer kmDriven,
        String location,
        String fuelType,
        String transmission,
        String engineType,
        String engine,
        String mileage,
        BigDecimal price,
        String description,
        String status,
        List<String> imageUrls,
        Instant createdAt,
        Instant updatedAt
) {}
