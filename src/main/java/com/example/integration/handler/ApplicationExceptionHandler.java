package com.example.integration.handler;

import com.example.integration.exception.ServiceException;
import com.example.integration.exception.ServiceResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Application Exception Handler.
 *
 * @author Anatolii Hamza
 */
@ControllerAdvice(basePackages = "com.example.integration.controller")
public class ApplicationExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceResponseBody> handleRentalException(ServiceException e) {
        return ResponseEntity.status(e.getResponseCode()).body(e.getResponseBody());
    }
}
