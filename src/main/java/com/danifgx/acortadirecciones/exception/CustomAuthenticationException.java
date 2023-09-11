package com.danifgx.acortadirecciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    private final HttpStatus httpStatus;

    public CustomAuthenticationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
