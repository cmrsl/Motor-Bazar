package com.bazar.car.repository;

import com.bazar.car.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:brand IS NULL OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
            "(:minYear IS NULL OR v.manufacturingYear >= :minYear) AND " +
            "(:maxYear IS NULL OR v.manufacturingYear <= :maxYear) AND " +
            "(:minPrice IS NULL OR v.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR v.price <= :maxPrice)")
    Page<Vehicle> search(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

}
