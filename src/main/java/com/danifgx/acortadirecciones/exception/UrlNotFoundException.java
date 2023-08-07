package com.danifgx.acortadirecciones.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String id) {
        super("No se pudo encontrar la URL con id: " + id);
    }
}