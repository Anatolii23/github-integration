package com.example.integration.exception;


import org.springframework.http.HttpStatus;

import static com.example.integration.enums.ApplicationException.USER_NOT_FOUND;

/**
 * User Not Found Exception.
 *
 * @author Anatolii Hamza
 */
public class UserNotFoundException extends ServiceException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getText());
    }
}
