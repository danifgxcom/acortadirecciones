package com.danifgx.acortadirecciones.service.validation.iface;

import com.danifgx.acortadirecciones.exception.UrlProcessingException;

public interface UrlValidator {

    public boolean validateUrl(String originalUrl);

    public void checkUrlLength(String originalUrl) throws UrlProcessingException;

    public void validateUrlSyntax(String originalUrl) throws UrlProcessingException;
}
