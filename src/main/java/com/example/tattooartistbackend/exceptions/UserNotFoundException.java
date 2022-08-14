package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.user.User;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {
        super(User.class.getSimpleName());
    }
}
