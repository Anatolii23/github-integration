package com.example.integration.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Identity Exception Enum.
 *
 * @author Anatolii Hamza
 */
@AllArgsConstructor
@Getter
public enum ApplicationException {

    USER_NOT_FOUND("404 http status code", "User not found"),
    REPOS_NOT_FOUND("404 http status code", "Repos not found"),
    ERROR_MSG ("409 http status code", "Server responded with error due to the reason: %s");

    private String code;
    private String text;
}
