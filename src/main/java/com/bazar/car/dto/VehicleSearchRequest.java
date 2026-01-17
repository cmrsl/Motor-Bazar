package com.bazar.car.dto;

import java.math.BigDecimal;

public record VehicleSearchRequest(

        String brand,
        String model,
        String fuelType,
        String location,
        Integer minYear,
        Integer maxYear,
        BigDecimal minPrice,
        BigDecimal maxPrice

) {
}
