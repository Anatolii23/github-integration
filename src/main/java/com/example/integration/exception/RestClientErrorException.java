package com.example.integration.exception;

import org.springframework.http.HttpStatus;

import static java.lang.String.format;

/**
 * The exception is thrown when external server responded with error code.
 *
 * @author Anatolii Hamza
 */
public class RestClientErrorException extends ServiceException {

    private static final String ERROR_MSG = "Server responded with error due to the reason: %s";

    public RestClientErrorException(String reason) {
        super(HttpStatus.CONFLICT, "409", format(ERROR_MSG, reason));
    }
}
