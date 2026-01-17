package com.bazar.car.helper;

import com.bazar.car.entity.Vehicle;
import com.bazar.car.entity.VehicleStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;


public class VehicleSpecification {

    public static Specification<Vehicle> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null : cb.equal(root.get("brand"), brand);
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(root.get("model"), model);
    }

    public static Specification<Vehicle> hasLocation(String location) {
        return (root, query, cb) ->
                location == null ? null : cb.equal(root.get("location"), location);
    }

    public static Specification<Vehicle> hasStatus(VehicleStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Vehicle> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null)
                return cb.between(root.get("price"), min, max);
            if (min != null)
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }


}
