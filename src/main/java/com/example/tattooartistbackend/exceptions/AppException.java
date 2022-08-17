package com.example.tattooartistbackend.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AppException extends RuntimeException {
   private HttpStatus httpStatus;

    public AppException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
