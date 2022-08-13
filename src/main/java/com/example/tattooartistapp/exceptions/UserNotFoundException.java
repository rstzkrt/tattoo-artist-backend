package com.example.tattooartistapp.exceptions;

import com.example.tattooartistapp.user.User;

import javax.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {
        super(User.class.getSimpleName());
    }
}
