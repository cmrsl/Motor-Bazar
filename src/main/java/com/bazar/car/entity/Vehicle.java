package com.bazar.car.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_vehicle_registration_number", columnNames = "registration_number")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Vehicle extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String description;

    @Column(columnDefinition = "VARCHAR(255)")
    private String brand;

    @Column(columnDefinition = "VARCHAR(255)")
    private String model;

    @Column(columnDefinition = "VARCHAR(255)")
    private Integer manufacturingYear;

    @Column(columnDefinition = "VARCHAR(255)")
    private String color;

    @Column(name = "registration_number", nullable = false, length = 255, unique = true)
    private String registrationNumber;

    private Integer kmDriven;

    @Column(columnDefinition = "VARCHAR(255)")
    private String location;

    @Column(columnDefinition = "VARCHAR(255)")
    private String fuelType;

    @Column(columnDefinition = "VARCHAR(255)")
    private String transmission;

    @Column(columnDefinition = "VARCHAR(255)")
    private String engineType;

    @Column(columnDefinition = "VARCHAR(255)")
    private String engine;

    @Column(columnDefinition = "VARCHAR(255)")
    private String mileage;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status; // active, sold, pending

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private List<String> images = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = VehicleStatus.ACTIVE;
        }
        if (this.registrationNumber != null) {
            this.registrationNumber = this.registrationNumber.trim().toUpperCase();
        }
    }

}
