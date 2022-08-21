package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.address.Address;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super(Address.class.getSimpleName()+ " not found !");
    }
}
