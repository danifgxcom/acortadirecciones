package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private UrlRecordService urlRecordService;

    @Mock
    private UrlVerifierService urlVerifierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://short.url/");
    }

    @Test
    void testShortenUrl() throws MalformedURLException {
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        String id = "randomId";

        when(idGeneratorService.generateRandomId()).thenReturn(id);
        when(urlRecordService.createUrlRecord(anyString(), anyString(), anyString(), anyInt())).thenReturn("http://short.url/randomId");

        try {
            String result = urlService.shortenUrl(originalUrl, expirationHours);
            assertEquals("http://short.url/randomId", result);
        } catch (UrlProcessingException e) {
            fail("No se esperaba la excepción UrlProcessingException");
        }
    }

    @Test
    void testShortenUrlWhenCreateUrlRecordThrowsException() throws MalformedURLException{
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        String id = "randomId";

        when(idGeneratorService.generateRandomId()).thenReturn(id);
        when(urlRecordService.createUrlRecord(anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new UrlProcessingException("Simulated exception"));

        try {
            String result = urlService.shortenUrl(originalUrl, expirationHours);
            fail("Expected UrlProcessingException to be thrown");
        } catch (UrlProcessingException e) {
            // The test will pass if this exception is caught
            assertTrue(e.getMessage().contains("Simulated exception"));
        }
    }



    @Test
    void testGetOriginalUrl() {
        String id = "randomId";
        when(urlRecordService.retrieveOriginalUrl(id)).thenReturn("http://example.com");

        String result = urlService.getOriginalUrl(id);

        assertEquals("http://example.com", result);
    }

    @Test
    void testGetOriginalUrl_UrlNotFound() {
        String id = "randomId";
        when(urlRecordService.retrieveOriginalUrl(id)).thenThrow(new UrlNotFoundException(id));

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(id));
    }

    @Test
    void testGetOriginalUrl_UrlExpired() {
        String id = "randomId";
        when(urlRecordService.retrieveOriginalUrl(id)).thenThrow(new UrlExpiredException(id));

        assertThrows(UrlExpiredException.class, () -> urlService.getOriginalUrl(id));
    }

}
