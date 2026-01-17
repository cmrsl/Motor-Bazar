package com.bazar.car.service;


import com.bazar.car.dto.*;
import com.bazar.car.entity.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;


public interface VehicleService {

    // Create Vehicle with Images
    VehicleResponseDto addVehicles(VehicleRequestDto dto, MultipartFile[] images) throws IOException;

    // Get All Vehicles
    Page<VehicleResponseDto> searchVehicles(VehicleSearchRequest request, Pageable pageable);

    // Get Vehicle by ID
    VehicleResponseDto getByRegistrationNumber(String registrationNumber);

    VehicleResponseDto updateByRegistrationNumber(String registrationNumber,VehicleRequestDto dto,MultipartFile[] images) throws IOException;

    void softDeleteByRegistrationNumber(String registrationNumber);

    void updateVehicleStatus(String registrationNumber,VehicleStatus status);

}
