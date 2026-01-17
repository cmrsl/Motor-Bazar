package com.bazar.car.controller;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import com.bazar.car.dto.VehicleSearchRequest;
import com.bazar.car.entity.VehicleStatus;
import com.bazar.car.helper.Validator;
import com.bazar.car.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {


    private final VehicleService vehicleService;
    private final Validator validator;


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<VehicleResponseDto> addVehicles(
            @Valid @RequestPart("vehicle") VehicleRequestDto dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        log.info("VehicleController:create");

        log.info("Validating vehicle data");
        validator.validateDTO(dto, images);

        VehicleResponseDto response = vehicleService.addVehicles(dto, images);

        log.info("VehicleController:create() -> Completed successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 2️⃣ GET ALL VEHICLES (PAGINATION + FILTERS)
    @GetMapping
    public ResponseEntity<Page<VehicleResponseDto>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            // optional filters
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {

        VehicleSearchRequest searchRequest =
                new VehicleSearchRequest(
                        brand,
                        model,
                        fuelType,
                        location,
                        minYear,
                        maxYear,
                        minPrice,
                        maxPrice
                );

        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleResponseDto> vehicles = vehicleService.searchVehicles(searchRequest,pageable);
        return ResponseEntity.ok(vehicles);
    }

    // 3️⃣ GET VEHICLE BY REGISTRATION NUMBER
    @GetMapping("/{registrationNumber}")
    public ResponseEntity<VehicleResponseDto> getVehicleByRegistrationNumber(
            @PathVariable String registrationNumber
    ) {

            log.info("API -> Get Vehicle {}", registrationNumber);
        return ResponseEntity.ok(
                vehicleService.getByRegistrationNumber(registrationNumber)
        );
    }


    // 4️⃣ UPDATE VEHICLE
    @PutMapping("/{registrationNumber}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(
            @PathVariable String registrationNumber,
            @Valid @RequestPart("vehicle") VehicleRequestDto request,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) throws Exception {

        log.info("API -> Update Vehicle {}", registrationNumber);
        return ResponseEntity.ok(
                vehicleService.updateByRegistrationNumber(registrationNumber, request, images)
        );
    }

    // 5️⃣ SOFT DELETE (INACTIVE)
    @DeleteMapping("/{registrationNumber}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable String registrationNumber
    ) {

        log.info("API -> Delete Vehicle {}", registrationNumber);
        vehicleService.softDeleteByRegistrationNumber(registrationNumber);
        return ResponseEntity.noContent().build();
    }

    // 6️⃣ UPDATE VEHICLE STATUS (ACTIVE / SOLD)
    @PatchMapping("/{registrationNumber}/status")
    public ResponseEntity<Void> updateVehicleStatus(
            @PathVariable String registrationNumber,
            @RequestParam VehicleStatus status
    ) {

        log.info("API -> Update Vehicle Status {}", registrationNumber);
        vehicleService.updateVehicleStatus(registrationNumber, status);
        return ResponseEntity.ok().build();
    }
}
