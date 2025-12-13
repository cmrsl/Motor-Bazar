package com.bazar.car.dto;

import java.math.BigDecimal;


public record VehicleRequestDto(
        String brand,
        String model,
        Integer manufacturingYear,
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
        Long dealerId,
        Long customerId
) {}
