package com.bazar.car.entity;

import jakarta.persistence.*;
import lombok.Data;


public enum Role {
    USER,
    ADMIN,
    CUSTOMER,
    DEALER
}