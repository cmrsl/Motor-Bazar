package com.bazar.car.controller;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import com.bazar.car.helper.Validator;
import com.bazar.car.service.VehicleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles/v1")
@Slf4j
public class VehicleController {


    private final VehicleService vehicleService;

    private final Validator validator;

    public VehicleController(VehicleService vehicleService, Validator validator) {
        this.vehicleService = vehicleService;
        this.validator = validator;
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<VehicleResponseDto> addVehicles(@RequestPart("vehicle") @Valid VehicleRequestDto dto,
                                                          @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        log.info("VehicleController:create");

        log.info("Validating vehicle data");
        validator.validateDTO(dto, images);

        VehicleResponseDto response = vehicleService.addVehicles(dto, images);

        log.info("VehicleController:create() -> Completed successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> search() {
        List<VehicleResponseDto> allVehicles = vehicleService.getAllVehicles();

        return ResponseEntity.ok(allVehicles);
    }


    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<VehicleResponseDto> update(@PathVariable Long id,
                                                     @RequestPart("vehicle") @Valid VehicleRequestDto dto,
                                                     @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
        return ResponseEntity.ok(vehicleService.update(id, dto, images));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {

        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
