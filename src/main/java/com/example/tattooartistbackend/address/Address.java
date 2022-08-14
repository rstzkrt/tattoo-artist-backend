package com.example.tattooartistbackend.address;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    private UUID id;
    private String street;
    private String city;
    private String country;
    private String postalCode;
    private String state;
    private String otherInformation;
}
