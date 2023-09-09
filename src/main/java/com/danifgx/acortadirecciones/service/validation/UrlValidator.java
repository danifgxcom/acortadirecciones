package com.danifgx.acortadirecciones.service.validation;

import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@Slf4j
public class UrlValidator {

    @Value("${url.max.length}")
    private int maxUrlLength;

    public boolean validateUrl(String originalUrl) {
        try {
            checkUrlLength(originalUrl);
            validateUrlSyntax(originalUrl);
            log.debug("Successful original URL validation");
            return true;
        }
        catch (UrlProcessingException e) {
            log.debug("Failed to validate original URL validation");
            return false;
        }
    }

    public void checkUrlLength(String originalUrl) throws UrlProcessingException {
        if (originalUrl.length() > maxUrlLength) {
            throw new UrlProcessingException("URL exceeds the maximum allowed length");
        }
    }

    public void validateUrlSyntax(String originalUrl) throws UrlProcessingException {
        try {
            new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new UrlProcessingException("Invalid URL provided: " + originalUrl, e);
        }
    }
}
