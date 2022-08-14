package com.example.tattooartistbackend.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    private String street;
    private String city;
    private String country;
    private String postalCode;
    private String state;
    private String otherInformation;
}
