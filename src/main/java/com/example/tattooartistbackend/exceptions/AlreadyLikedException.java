package com.example.tattooartistbackend.exceptions;

public class AlreadyLikedException extends  RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     */
    public AlreadyLikedException() {
        super("User has already liked the tattooWork");
    }
}
