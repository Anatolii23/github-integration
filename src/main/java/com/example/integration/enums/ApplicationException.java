package com.example.integration.enums;

/**
 * Identity Exception Enum.
 *
 * @author Anatolii Hamza
 */
public enum ApplicationException {

    USER_NOT_FOUND("404 http status code", "User not found"),
    REPOS_NOT_FOUND("404 http status code", "Repos not found");

    private String code;
    private String text;

    ApplicationException(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
