package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

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
    void testShortenUrl() {
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        String id = "randomId";

        when(idGeneratorService.generateRandomId()).thenReturn(id);
        when(urlRecordService.createUrlRecord(anyString(), anyString(), anyString(), anyInt())).thenReturn("http://short.url/randomId");

        String result = urlService.shortenUrl(originalUrl, expirationHours);

        assertEquals("http://short.url/randomId", result);
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

    // ... Más tests
}
