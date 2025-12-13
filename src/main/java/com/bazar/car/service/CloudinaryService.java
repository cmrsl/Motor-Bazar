package com.bazar.car.service;

import com.bazar.car.exception.ApiValidationException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public List<String> uploadMultipleImages(MultipartFile[] files) {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            uploadedUrls.add(uploadImage(file));
        }

        return uploadedUrls;
    }

    public String uploadImage(MultipartFile file) {
        try {
            log.info("Uploading image: {}", file.getOriginalFilename());

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "vehicle_images"));

            String imageUrl = uploadResult.get("secure_url").toString();
            log.info("Image uploaded successfully: {}", imageUrl);

            return imageUrl;

        } catch (IOException ex) {
            log.error("Image upload failed: {}", ex.getMessage());
            throw new ApiValidationException(HttpStatus.BAD_REQUEST, "Upload Failed",
                    "Unable to upload image: " + file.getOriginalFilename());
        }
    }

    public void deleteImagesByVehicleId(List<String> vehicleImageUrls) {
        //validate input then delete images one by one from cloudinary and return true if all deleted successfully
        Map result = null;
        for (String imageUrl : vehicleImageUrls) {
            String publicId = extractPublicIdFromUrl(imageUrl);
            try {
                result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Deleted image with Public ID: {}. Result: {}", publicId, result);
            } catch (IOException e) {
                log.error("Failed to delete image with Public ID: {}. Error: {}", publicId, e.getMessage());

            }
        }
    }

    public String extractPublicIdFromUrl(String imageUrl) throws IllegalArgumentException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty.");
        }

        try {
            URL url = new URL(imageUrl);
            String path = url.getPath();

            // Cloudinary paths follow the pattern:
            // /<cloud_name>/<resource_type>/<type>/<version>/<public_id>.<format>

            // We need to find the index of the version component, which is 'v' followed by 10 digits
            // The public ID starts right after this version component.
            Pattern pattern = Pattern.compile("v\\d{10,}/");
            Matcher matcher = pattern.matcher(path);

            if (matcher.find()) {
                // The Public ID starts at the end of the version string
                int publicIdStartIndex = matcher.end();

                // Get the string segment from the start index to the end
                String publicIdWithFormat = path.substring(publicIdStartIndex);

                // Remove the file extension (the last part after the last dot)
                int lastDotIndex = publicIdWithFormat.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    return publicIdWithFormat.substring(0, lastDotIndex);
                }

                // Should only happen if there is no file extension, return the whole remaining path
                return publicIdWithFormat;
            } else {
                throw new IllegalArgumentException("Could not find the version number in the URL path, which is required to extract Public ID.");
            }

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The provided string is not a valid URL.", e);
        }
    }



}
