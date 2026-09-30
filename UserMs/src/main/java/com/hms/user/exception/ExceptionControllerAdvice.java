package com.hms.user.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    Environment environment;

    /// Custom HMS Exception
    @ExceptionHandler(HMSException.class)
    public ResponseEntity<ErrorInfo> hmsException(HMSException e) {
        ErrorInfo errorInfo = new ErrorInfo(
                environment.getProperty(e.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /// Handles all unexpected exceptions (Global Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> globalException(Exception e) {
        ErrorInfo errorInfo = new ErrorInfo(
                "Some Error Occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}