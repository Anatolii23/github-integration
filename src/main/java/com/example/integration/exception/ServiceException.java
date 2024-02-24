package com.example.integration.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Service Exception.
 *
 * @author Anatolii Hamza
 */
@Getter
public class ServiceException extends Exception {

    private final HttpStatus responseCode;
    private final ServiceResponseBody responseBody;

    public ServiceException(HttpStatus responseCode, String errorCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.responseBody = new ServiceResponseBody(message, errorCode);
    }
}
