package com.example.tattooartistbackend.exceptions;

public class UserArtistPageNotFoundException extends RuntimeException{

    public UserArtistPageNotFoundException() {
        super("User doesn't have an artist page to post tattoo work");
    }

}
