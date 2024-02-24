package com.example.integration.exception;

import org.springframework.http.HttpStatus;

import static com.example.integration.enums.ApplicationException.ERROR_MSG;
import static java.lang.String.format;

/**
 * The exception is thrown when external server responded with error code.
 *
 * @author Anatolii Hamza
 */
public class RestClientErrorException extends ServiceException {

    public RestClientErrorException(String reason) {
        super(HttpStatus.CONFLICT, ERROR_MSG.getCode(), format(ERROR_MSG.getText(), reason));
    }
}
