package com.elitefolk.authservice.controllerAdvices;

import com.elitefolk.authservice.dtos.GlobalErrorDto;
import com.elitefolk.authservice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalErrorDto<String>> handleUserNotFoundException(UserNotFoundException ex) {
        GlobalErrorDto<String> error = new GlobalErrorDto<>(ex.getMessage(), HttpStatus.NOT_FOUND.toString(), ex.getDetails());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalErrorDto<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        GlobalErrorDto<String> error = new GlobalErrorDto<>(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.toString(),
                "Request needs to have proper parameters"
        );
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorDto<String>> handleGenericException(Exception ex) {
        GlobalErrorDto<String> error = new GlobalErrorDto<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        return ResponseEntity.status(500).body(error);
    }
}
