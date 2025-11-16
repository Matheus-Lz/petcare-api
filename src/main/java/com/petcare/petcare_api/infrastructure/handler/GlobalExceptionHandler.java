package com.petcare.petcare_api.infrastructure.handler;

import com.petcare.petcare_api.infrastructure.exception.UserExceptions;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.petcare.petcare_api.application.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), "Acesso negado.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(UserExceptions.InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrentPasswordException(UserExceptions.InvalidCurrentPasswordException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserExceptions.CurrentPasswordRequiredException.class)
    public ResponseEntity<ErrorResponse> handleCurrentPasswordRequiredException(UserExceptions.CurrentPasswordRequiredException ex) {
        var errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Ocorreu um erro inesperado.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
