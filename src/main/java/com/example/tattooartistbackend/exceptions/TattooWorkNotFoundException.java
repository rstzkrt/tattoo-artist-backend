package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.tattooWork.TattooWork;

public class TattooWorkNotFoundException extends RuntimeException {

    public TattooWorkNotFoundException() {
        super(TattooWork.class.getSimpleName()+ " not found !");
    }
}
