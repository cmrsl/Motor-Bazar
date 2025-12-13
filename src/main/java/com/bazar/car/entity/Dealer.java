package com.bazar.car.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "dealers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String email;
    private String address;
    private String company;

}
