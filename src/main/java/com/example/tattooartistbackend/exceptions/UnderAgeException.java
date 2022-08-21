package com.example.tattooartistbackend.exceptions;


public class UnderAgeException extends RuntimeException {
    public UnderAgeException() {
        super("The user is under age. should be below 18 to create an artist page");
    }
}
