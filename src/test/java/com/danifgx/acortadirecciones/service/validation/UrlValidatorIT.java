package com.danifgx.acortadirecciones.service.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UrlValidatorIT {

    @Autowired
    private UrlValidatorImpl urlValidator;

    @Test
    void testValidUrlIntegration() {
        assertTrue(urlValidator.validateUrl("https://www.danifgx.com"));
    }

    @Test
    void testInvalidUrlSyntaxIntegration() {
        assertFalse(urlValidator.validateUrl("invalid-url"));
    }

    @Test
    void testUrlExceedsMaxLengthIntegration() {
        String longUrl = "https://www.google.com/" + "a".repeat(3000);  // Assuming max length is less than 3000
        assertFalse(urlValidator.validateUrl(longUrl));
    }
}
