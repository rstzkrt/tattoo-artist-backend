package com.example.tattooartistbackend.exceptions;

import com.example.tattooartistbackend.generated.models.ErrorResponse;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ResponseStatus(HttpStatus.NOT_FOUND)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            TattooWorkNotFoundException.class,
            AddressNotFoundException.class,
            CommentNotFoundException.class,
            ReviewNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(RuntimeException exception, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            UserArtistPageNotFoundException.class,
            AlreadyLikedException.class,
            AlreadyDislikedException.class,
            UnderAgeException.class,
            CreateReviewNotAllowdException.class,
            TattooWorkCommentExistsException.class,
            NotOwnerOfEntityException.class
    })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorResponse> handle(RuntimeException exception, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(status.value());
        errorResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
//    @ExceptionHandler({
//            FirebaseAuthException.class,
//    })
//    public ResponseEntity<Object> firebaseExc(FirebaseAuthException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setCode(status.value());
//        errorResponse.setMessage(ex.getMessage());
//        return new ResponseEntity<>(errorResponse, status);
//    }
}
