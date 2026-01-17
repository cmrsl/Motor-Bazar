package com.bazar.car.repository;

import com.bazar.car.entity.Vehicle;
import com.bazar.car.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("""
           UPDATE Vehicle v
           SET v.status = :status
           WHERE v.registrationNumber = :regNo
           """)
    int updateStatusByRegistrationNumber(
            @Param("regNo") String registrationNumber,
            @Param("status") VehicleStatus status
    );

}
