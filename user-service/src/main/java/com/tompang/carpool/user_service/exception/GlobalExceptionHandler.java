package com.tompang.carpool.user_service.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.tompang.carpool.user_service.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
        ResourceNotFoundException ex, HttpServletRequest request
    ) {

        logger.warn("Resource not found: {}", ex.getMessage(), ex); // Log as WARN

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(
        BadRequestException ex, HttpServletRequest request
    ) {

        logger.warn("Bad request: {}", ex.getMessage(), ex);

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentialsException(
        InvalidCredentialsException ex, HttpServletRequest request
    ) {

        logger.warn("Invalid credentials: {}", ex.getMessage(), ex);

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(UniqueConstraintException.class)
    public ResponseEntity<ErrorResponseDto> handleUniqueConstraintException(
        UniqueConstraintException ex, HttpServletRequest request
    ) {

        logger.warn("Unique constraint violation: {}", ex.getMessage(), ex);

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error: {}", ex.getMessage(), ex); // Log as ERROR
        
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Validation failed: {}", validationErrors);

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                validationErrors,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDto> handleMissingRequestParameterException(
        Exception ex, HttpServletRequest request
    ) {
        
        logger.warn("Invalid request parameter: {}", ex.getMessage(), ex);

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
