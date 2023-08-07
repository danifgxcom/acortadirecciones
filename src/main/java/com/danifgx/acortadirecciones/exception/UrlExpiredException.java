package com.danifgx.acortadirecciones.exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String id) {
        super("La URL con id: " + id + " ha expirado");
    }
}