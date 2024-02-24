package com.example.integration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Service Response Body.
 *
 * @author Amatolii Hamza
 */
@Getter
@AllArgsConstructor
public class ServiceResponseBody {

    private String message;
    private String code;
}
