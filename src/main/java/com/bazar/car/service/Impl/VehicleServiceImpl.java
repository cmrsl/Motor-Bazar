package com.bazar.car.service.Impl;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import com.bazar.car.dto.VehicleSearchRequest;
import com.bazar.car.entity.*;
import com.bazar.car.exception.ApiValidationException;
import com.bazar.car.helper.VehicleSpecification;
import com.bazar.car.repository.*;

import com.bazar.car.service.CloudinaryService;

import com.bazar.car.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final CloudinaryService cloudinaryService;
    private final DealerRepository dealerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public VehicleResponseDto addVehicles(VehicleRequestDto dto, MultipartFile[] images) throws IOException {

        log.info("VehicleService:create() -> Processing vehicle creation");

        if (vehicleRepository.existsByRegistrationNumber(dto.registrationNumber())){
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "DUPLICATE_VEHICLE",
                    "A vehicle with the registration number " + dto.registrationNumber() + " already exists.");
        }

        Vehicle vehicle = new Vehicle();
        mapDtoToEntity(dto, vehicle);

        if (images != null && images.length > 0){
            log.info("VehicleService:addVehicles -> Uploading images");
            List<String> imageUrls = cloudinaryService.uploadMultipleImages(images);
            vehicle.setImages(imageUrls);
        }

        attachDealerAndCustomer(dto, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle saved successfully with ID: {}", saved.getId());
        return mapToDto(saved);

    }

    //============= SEARCH (PAGINATION & FILTERING)  =============
    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDto> searchVehicles(VehicleSearchRequest req,Pageable pageable) {
        log.info("VehicleService:searchVehicles() -> Searching vehicles with filters: {}", req);

        Specification<Vehicle> spec = (root, query, cb) -> cb.conjunction();

        if (req.brand() != null) {
            spec = spec.and(VehicleSpecification.hasBrand(req.brand()));
        }

        if (req.model() != null) {
            spec = spec.and(VehicleSpecification.hasModel(req.model()));
        }

        if (req.location() != null) {
            spec = spec.and(VehicleSpecification.hasLocation(req.location()));
        }

//        if (req.status() != null) {
//            spec = spec.and(VehicleSpecification.hasStatus(req.status()));
//        }

        if (req.minPrice() != null || req.maxPrice() != null) {
            spec = spec.and(
                    VehicleSpecification.priceBetween(
                            req.minPrice(),
                            req.maxPrice()
                    )
            );
        }

        return vehicleRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }



    // ================= GET BY REGISTRATION =================
    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDto getByRegistrationNumber(String registrationNumber) {

        Vehicle vehicle = vehicleRepository
                .findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ApiValidationException(
                        HttpStatus.NOT_FOUND,
                        "VEHICLE_NOT_FOUND",
                        "Vehicle not found with registration number: " + registrationNumber
                ));

        return mapToDto(vehicle);
    }


    // ================= UPDATE =================

    @Override
    public VehicleResponseDto updateByRegistrationNumber(
            String registrationNumber,
            VehicleRequestDto dto,
            MultipartFile[] images
    ) throws IOException {

        Vehicle vehicle = vehicleRepository
                .findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ApiValidationException(
                        HttpStatus.NOT_FOUND,
                        "VEHICLE_NOT_FOUND",
                        "Vehicle not found with registration number: " + registrationNumber
                ));

        mapDtoToEntity(dto, vehicle);

        if (images != null && images.length > 0) {
            List<String> uploaded = cloudinaryService.uploadMultipleImages(images);
            List<String> existing = vehicle.getImages() != null
                    ? vehicle.getImages()
                    : new ArrayList<>();
            existing.addAll(uploaded);
            vehicle.setImages(existing);
        }

        attachDealerAndCustomer(dto, vehicle);

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToDto(saved);
    }

    // ================= SOFT DELETE =================
    @Override
    public void softDeleteByRegistrationNumber(String registrationNumber) {

        int updated = vehicleRepository.updateStatusByRegistrationNumber(
                registrationNumber,
                VehicleStatus.INACTIVE
        );

        if (updated == 0) {
            throw new ApiValidationException(
                    HttpStatus.NOT_FOUND,
                    "VEHICLE_NOT_FOUND",
                    "Vehicle not found with registration number: " + registrationNumber
            );
        }
    }

    // ================= STATUS UPDATE =================
    @Override
    public void updateVehicleStatus(
            String registrationNumber,
            VehicleStatus status
    ) {

        int updated = vehicleRepository.updateStatusByRegistrationNumber(
                registrationNumber,
                status
        );

        if (updated == 0) {
            throw new ApiValidationException(
                    HttpStatus.NOT_FOUND,
                    "VEHICLE_NOT_FOUND",
                    "Vehicle not found with registration number: " + registrationNumber
            );
        }
    }


    // ================= HELPERS =================

    private void attachDealerAndCustomer(VehicleRequestDto dto, Vehicle vehicle) {

        if (dto.dealerId() != null) {
            Dealer dealer = dealerRepository.findById(dto.dealerId()).orElse(null);
            vehicle.setDealer(dealer);
        }

        if (dto.customerId() != null) {
            Customer customer = customerRepository.findById(dto.customerId()).orElse(null);
            vehicle.setCustomer(customer);
        }
    }

    private void mapDtoToEntity(VehicleRequestDto dto, Vehicle vehicle) {

        if (dto.brand() != null) vehicle.setBrand(dto.brand());
        if (dto.model() != null) vehicle.setModel(dto.model());
        if (dto.manufacturingYear() != null) vehicle.setManufacturingYear(dto.manufacturingYear());
        if (dto.color() != null) vehicle.setColor(dto.color());
        if (dto.price() != null) vehicle.setPrice(dto.price());
        if (dto.engineType() != null) vehicle.setEngineType(dto.engineType());
        if (dto.transmission() != null) vehicle.setTransmission(dto.transmission());
        if (dto.fuelType() != null) vehicle.setFuelType(dto.fuelType());
        if (dto.location() != null) vehicle.setLocation(dto.location());
        if (dto.kmDriven() != null) vehicle.setKmDriven(dto.kmDriven());
        if (dto.engine() != null) vehicle.setEngine(dto.engine());
        if (dto.mileage() != null) vehicle.setMileage(dto.mileage());
        if (dto.description() != null) vehicle.setDescription(dto.description());

        // registrationNumber is immutable after create
        if (vehicle.getRegistrationNumber() == null && dto.registrationNumber() != null) {
            vehicle.setRegistrationNumber(dto.registrationNumber());
        }
    }

    private VehicleResponseDto mapToDto(Vehicle v) {

        return new VehicleResponseDto(
                v.getId(),
                v.getBrand(),
                v.getModel(),
                v.getManufacturingYear(),
                v.getColor(),
                v.getRegistrationNumber(),
                v.getKmDriven(),
                v.getLocation(),
                v.getFuelType(),
                v.getTransmission(),
                v.getEngineType(),
                v.getEngine(),
                v.getMileage(),
                v.getPrice(),
                v.getDescription(),
                v.getStatus().name(),
                v.getImages(),
                v.getCreatedDate(),
                v.getLastModifiedDate()
        );
    }

}
