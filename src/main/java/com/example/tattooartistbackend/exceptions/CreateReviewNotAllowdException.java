package com.example.tattooartistbackend.exceptions;

public class CreateReviewNotAllowdException extends  RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     */
    public CreateReviewNotAllowdException() {
        super("YOU CANNOT POST REVIEW FOR YOURSELF");
    }
}
