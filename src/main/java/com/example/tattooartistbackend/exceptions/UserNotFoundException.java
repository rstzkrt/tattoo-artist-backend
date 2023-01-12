package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.user.User;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(User.class.getSimpleName()+ " not found !");
    }
}
