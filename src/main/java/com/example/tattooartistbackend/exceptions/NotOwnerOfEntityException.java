package com.example.tattooartistbackend.exceptions;

public class NotOwnerOfEntityException extends  RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     */
    public NotOwnerOfEntityException(String message) {
        super(message);
    }
}
