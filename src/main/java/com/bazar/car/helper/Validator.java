package com.bazar.car.helper;

import com.bazar.car.dto.VehicleRequestDto;
import com.bazar.car.exception.ApiValidationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class Validator {

    /* Validates the VehicleRequestDto and associated images.
     * Throws ApiValidationException if any validation fails.
     */
    public void validateDTO(@Valid VehicleRequestDto dto, MultipartFile[] images) {
        log.info("VehicleController:validateDTO - Validating VehicleRequestDto");
        if (dto.brand() == null || dto.brand().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_BRAND", "Brand cannot be null or blank");
        }
        if (dto.model() == null || dto.model().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_MODEL", "Model cannot be null or blank");
        }
        if (dto.manufacturingYear() < 1886 || dto.manufacturingYear() > 2100) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_MANUFACTURING_YEAR", "Year must be between 1886 and 2026");
        }

        if (dto.color() == null || dto.color().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_COLOR", "Color cannot be null or blank");
        }
        if (dto.registrationNumber() == null || dto.registrationNumber().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_REGISTRATION_NUMBER", "Registration number cannot be null or blank");
        }
        if (dto.kmDriven() < 0) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_KM_DRIVEN", "Kilometers driven cannot be negative");
        }
        if (dto.price().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_PRICE", "Price cannot be negative");
        }
        if (dto.location() == null || dto.location().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_LOCATION", "Location cannot be null or blank");
        }
        if (dto.fuelType() == null || dto.fuelType().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_FUEL_TYPE", "Fuel type cannot be null or blank");
        }
        if (dto.transmission() == null || dto.transmission().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_TRANSMISSION", "Transmission cannot be null or blank");
        }
        if (dto.engineType() == null || dto.engineType().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_ENGINE_TYPE", "Engine type cannot be null or blank");
        }
        if (dto.engine() == null || dto.engine().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_ENGINE", "Engine cannot be null or blank");
        }
        if (dto.mileage() == null || dto.mileage().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_MILEAGE", "Mileage cannot be null or blank");
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_DESCRIPTION", "Description cannot be null or blank");
        }
        if (dto.dealerId() == null || dto.dealerId() <= 0) {
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_DEALER_ID", "Dealer ID must be a positive number");
        }

        // Validate images if provided
        imageValidator(images);
        log.info("VehicleController:validateDTO - Validating VehicleRequestDto Completed Successfully");
    }

    private void imageValidator(MultipartFile[] images) {
        log.info("VehicleController:imageValidator - Validating image files");
        if (images != null) {
            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_IMAGE", "Image file cannot be empty");
                }
                String contentType = image.getContentType();
                log.info("Validating image with content type: {}", contentType);
//                if (contentType == null ||
//                    !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif")
//                            || contentType.equals("image/jpg"))) {
//                    throw new ApiValidationException(HttpStatus.BAD_REQUEST, "INVALID_IMAGE_TYPE", "Only JPEG, PNG, and GIF images are allowed");
//                }
                if (image.getSize() > 5 * 1024 * 1024) { // 5 MB size limit
                    throw new ApiValidationException(HttpStatus.BAD_REQUEST, "IMAGE_TOO_LARGE", "Image size cannot exceed 5 MB");
                }
            }
            log.info("VehicleController:imageValidator - Validating image files Completed Successfully");
        }
    }
}
