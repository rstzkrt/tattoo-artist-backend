package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.review.Review;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super(Review.class.getSimpleName()+ " not found !");
    }
}
