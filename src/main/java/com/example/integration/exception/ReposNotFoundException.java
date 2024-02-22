package com.example.integration.exception;

import org.springframework.http.HttpStatus;

import static com.example.integration.enums.ApplicationException.REPOS_NOT_FOUND;

/**
 * Users Repos Not Found Exception.
 *
 * @author Anatolii Hamza
 */
public class ReposNotFoundException extends ServiceException {

    public ReposNotFoundException() {
        super(HttpStatus.NOT_FOUND, REPOS_NOT_FOUND.getCode(), REPOS_NOT_FOUND.getText());
    }
}
