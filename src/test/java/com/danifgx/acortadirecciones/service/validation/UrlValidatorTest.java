package com.danifgx.acortadirecciones.service.validation;

import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlValidatorTest {


    private UrlValidatorImpl urlValidator;

    @BeforeEach
    public void setUp() {
        urlValidator = new UrlValidatorImpl();
        urlValidator.setMaxUrlLength(500); // Establecer manualmente
    }
    @Test
    void testValidUrl() {
        assertTrue(urlValidator.validateUrl("https://www.google.com"));
    }

    @Test
    void testInvalidUrlSyntax() {
        assertThrows(UrlProcessingException.class, () -> urlValidator.validateUrlSyntax("invalid-url"));
    }

    @Test
    void testUrlExceedsMaxLength() {
        String longUrl = "https://www.google.com/" + "a".repeat(2000);
        assertThrows(UrlProcessingException.class, () -> urlValidator.checkUrlLength(longUrl));
    }
}
