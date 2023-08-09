package com.danifgx.acortadirecciones.exception;

public class UrlProcessingException extends RuntimeException {
    public UrlProcessingException(String message) {
        super(message);
    }

    public UrlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}