package com.bazar.car.service.Impl;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.dto.VehicleResponseDto;
import com.bazar.car.entity.*;
import com.bazar.car.exception.ApiValidationException;
import com.bazar.car.exception.VehicleNotFoundException;
import com.bazar.car.repository.*;

import com.bazar.car.service.CloudinaryService;
import com.bazar.car.service.StorageService;

import com.bazar.car.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final StorageService storageService;


    @Override
    public VehicleResponseDto addVehicles(VehicleRequestDto dto, MultipartFile[] images) throws IOException {

        log.info("VehicleService:create() -> Processing vehicle creation");

        try{

            Vehicle vehicle = new Vehicle();
            mapDtoToEntity(dto, vehicle);

            if(images != null && images.length > 0){
                log.info("VehicleService:addVehicles -> Uploading images");
                List<String> imageUrls = cloudinaryService.uploadMultipleImages(images);
                vehicle.setImages(imageUrls);
            }

            if (dto.dealerId() != null) {
                Dealer d = dealerRepository.findById(dto.dealerId()).orElse(null);
                vehicle.setDealer(d);
            }
            if (dto.customerId() != null) {
                Customer c = customerRepository.findById(dto.customerId()).orElse(null);
                vehicle.setCustomer(c);
            }

            Vehicle saved = vehicleRepository.save(vehicle);
            log.info("Vehicle saved successfully with ID: " + saved.getId());
            return mapToDto(saved);

        }catch (ApiValidationException e){
            log.error("VehicleService:addVehicles -> Vehicle Exception");
            throw e;
        } catch (Exception ex) {
            log.error("Unexpected error occurred during vehicle creation: {}", ex.getMessage(), ex);
            throw new ApiValidationException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Vehicle Creation Failed",
                    "An unexpected error occurred while creating vehicle");
        }
    }


    @Override
    public List<VehicleResponseDto> getAllVehicles() {
        log.info("VehicleService:getAllVehicles() -> Fetching all vehicles");


       List<Vehicle> vehicles = vehicleRepository.findAll();
       log.info("Total vehicles found: {}", vehicles.size());

        if (vehicles.isEmpty()) {
            throw new ApiValidationException(HttpStatus.NOT_FOUND,
                    "No Vehicles Found",
                    "There are no vehicles available in the system.");
        }

        log.info("Vehicle:getAllVehicles() -> Successfully fetched all vehicles");
        return vehicles.stream()
                .map(this::mapToDto)
                .toList();
    }


    @Override
    public VehicleResponseDto getById(Long id) {
        log.info("VehicleService:getById() -> Fetching vehicle with ID: {}", id);


        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ApiValidationException(HttpStatus.NOT_FOUND,"Vehicle Not Found", "Vehicle not found with id: " + id));

        log.info("Vehicle:getById() -> Successfully fetched vehicle with ID: {}", id);
        return mapToDto(vehicle);
    }




    @Override
    public VehicleResponseDto update(Long id, VehicleRequestDto dto, MultipartFile[] images) throws IOException {

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ApiValidationException(
                            HttpStatus.NOT_FOUND,"Vehicle Not Found",
                            "Vehicle not found with id: " + id));

        mapDtoToEntity(dto, vehicle);

        // if images provided, append them
        if (images != null) {
            log.info("VehicleService:update() -> Uploading new images for vehicle ID: {}", id);
            List<String> imageUrls = cloudinaryService.uploadMultipleImages(images);
            List<String> existingImages = vehicle.getImages();
            existingImages.addAll(imageUrls);
            vehicle.setImages(existingImages);
            log.info("Vehicle:update() -> Successfully updated images for vehicle ID: {}", id);
        }

        Vehicle saved = vehicleRepository.save(vehicle);

        log.info("Vehicle saved successfully with ID: " + saved.getId());

        return mapToDto(saved);
    }


    @Override
    public void delete(Long id) throws IOException {
        log.info("VehicleService:delete() -> Deleting vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ApiValidationException(
                HttpStatus.NOT_FOUND,"Vehicle Not Found",
                "Vehicle not found with id: " + id));

        // Proceed to delete
        log.info("VehicleService:delete() -> Vehicle found. Proceeding to delete vehicle with ID: {}", id);

        if (vehicle.getImages() != null) {
            cloudinaryService.deleteImagesByVehicleId(vehicle.getImages());
        }
        //delete images from cloudinary
        vehicleRepository.deleteById(id);
    }


    // helper mapping
    private void mapDtoToEntity(VehicleRequestDto dto, Vehicle vehicle) {
        if (dto.brand() != null) vehicle.setBrand(dto.brand());
        if (dto.model() != null) vehicle.setModel(dto.model());
        if (dto.manufacturingYear() != null) vehicle.setManufacturingYear(dto.manufacturingYear());
        if (dto.color() != null) vehicle.setColor(dto.color());
        if (dto.price() != null) vehicle.setPrice(dto.price());
        if (dto.engineType() != null) vehicle.setEngineType(dto.engineType());
        if (dto.transmission() != null) vehicle.setTransmission(dto.transmission());
        if (dto.model() != null) vehicle.setModel(dto.fuelType());
        if (dto.location() != null) vehicle.setLocation(dto.location());
        if (dto.kmDriven() != null) vehicle.setKmDriven(dto.kmDriven());
        if (dto.registrationNumber() != null) vehicle.setRegistrationNumber(dto.registrationNumber());
        if (dto.engine() != null) vehicle.setEngine(dto.engine());
        if (dto.mileage() != null) vehicle.setMileage(dto.mileage());
        if (dto.price() != null) vehicle.setPrice(dto.price());
        if (dto.description() != null) vehicle.setDescription(dto.description());
    }

    private VehicleResponseDto mapToDto(Vehicle vehicle) {
        return new VehicleResponseDto(
                vehicle.getId(), vehicle.getBrand(), vehicle.getModel(), vehicle.getManufacturingYear(), vehicle.getColor(),
                vehicle.getRegistrationNumber(), vehicle.getKmDriven(), vehicle.getLocation(), vehicle.getFuelType(), vehicle.getTransmission(),
                vehicle.getEngineType(), vehicle.getEngine(), vehicle.getMileage(), vehicle.getPrice(), vehicle.getDescription(), vehicle.getStatus(),
                vehicle.getImages(), vehicle.getCreatedDate(), vehicle.getLastModifiedDate()
        );
    }

}
