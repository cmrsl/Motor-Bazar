package com.bazar.car.service;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


public interface VehicleService {

    // Create Vehicle with Images
    VehicleResponseDto addVehicles(VehicleRequestDto dto, MultipartFile[] images) throws IOException;

    // Get All Vehicles
    List<VehicleResponseDto> getAllVehicles();

    // Get Vehicle by ID
    VehicleResponseDto getById(Long id);

    // Update Vehicle with Images
    VehicleResponseDto update(Long id, VehicleRequestDto dto, MultipartFile[] images) throws IOException;

    // Delete Vehicle by ID
    void delete(Long id) throws IOException;

}
