package com.example.integration.exception;

/**
 * Service Response Body.
 *
 * @author Amatolii Hamza
 */
public class ServiceResponseBody {

    private String message;
    private String code;

    public ServiceResponseBody() {
    }

    public ServiceResponseBody(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
