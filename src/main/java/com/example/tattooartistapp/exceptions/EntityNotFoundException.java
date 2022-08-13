package com.example.tattooartistapp.exceptions;


import org.springframework.http.HttpStatus;

public abstract class EntityNotFoundException extends AppException{

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message + "not found !");
    }
}
