package com.insyte.questionspark.backend.infrastructure.adapter.rest.exception;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.insyte.questionspark.backend.domain.exception.ServiceException;
import com.insyte.questionspark.backend.domain.exception.StoryNotFoundException;
import com.insyte.questionspark.backend.infrastructure.adapter.rest.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        ErrorResponse error = new ErrorResponse(
            "001",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error); 
    }
    
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOGGER.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
            String.valueOf(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
            , "Unsupported media type: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            "Resource not found: " + ex.getRequestURL()
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error);
    }

    @ExceptionHandler(StoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(StoryNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            ex.getMessage()
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid UUID format: %s", ex.getValue());
        ErrorResponse error = new ErrorResponse(
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            message
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error);
    }
}