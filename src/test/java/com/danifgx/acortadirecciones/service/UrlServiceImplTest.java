package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.iface.UrlLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlServiceImpl;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

    @Mock
    private UrlRecordServiceImpl urlRecordServiceImpl;

    @Mock
    private UrlVerifierService urlVerifierService;
    @Mock
    private Url mockUrl;

    @Mock
    private UrlLogService urlLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(urlServiceImpl, "baseUrl", "http://short.url/");
        ReflectionTestUtils.setField(urlServiceImpl, "urlLogService", urlLogService); // set the mock
    }

    @Test
    void testShortenUrl() throws MalformedURLException, UrlProcessingException {
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        int length = 32;
        String id = "randomId";
        Url mockUrl = mock(Url.class);
        UrlRepository mockUrlRepository = mock(UrlRepository.class);

        when(utilsServiceImpl.generateRandomId(length)).thenReturn(id);
        when(mockUrl.getId()).thenReturn("someId");
        when(mockUrl.getOriginalUrl()).thenReturn(originalUrl);
        when(mockUrl.getShortenedUrlId()).thenReturn(id);
        when(mockUrl.getShortenedBaseUrl()).thenReturn("http://short.url");

        when(mockUrlRepository.save(any(Url.class))).thenReturn(mockUrl);
        when(urlRecordServiceImpl.createUrlRecord(anyString(), anyString(), anyString(), anyInt())).thenReturn(mockUrl);
        when(utilsServiceImpl.generateShortUrl(anyString(), anyString())).thenReturn("http://short.url/" + id);

        doNothing().when(urlVerifierService).verifyUrl(anyString());

        try {
            String result = urlServiceImpl.shortenUrl(originalUrl, expirationHours, length);
            assertNotNull(result);
            assertEquals("http://short.url/" + id, result);
        } catch (UrlProcessingException e) {
            fail("No se esperaba la excepción UrlProcessingException");
        }
    }


    @Test
    void testShortenUrlWhenCreateUrlRecordThrowsException() throws MalformedURLException {
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        int length = 32;
        String id = "randomId";

        when(utilsServiceImpl.generateRandomId(length)).thenReturn(id);
        when(urlRecordServiceImpl.createUrlRecord(anyString(), anyString(), anyString(), anyInt()))
                .thenThrow(new UrlProcessingException("Simulated exception"));

        try {
            String result = urlServiceImpl.shortenUrl(originalUrl, expirationHours, length);
            fail("Expected UrlProcessingException to be thrown");
        } catch (UrlProcessingException e) {
            // The test will pass if this exception is caught
            assertTrue(e.getMessage().contains("Simulated exception"));
        }
    }


    @Test
    void testGetOriginalUrl() {
        String id = "randomId";
        when(urlRecordServiceImpl.retrieveOriginalUrl(id)).thenReturn("http://example.com");

        String result = urlServiceImpl.getOriginalUrl(id);

        assertEquals("http://example.com", result);
    }

    @Test
    void testGetOriginalUrl_UrlNotFound() {
        String id = "randomId";
        when(urlRecordServiceImpl.retrieveOriginalUrl(id)).thenThrow(new UrlNotFoundException(id));

        assertThrows(UrlNotFoundException.class, () -> urlServiceImpl.getOriginalUrl(id));
    }

    @Test
    void testGetOriginalUrl_UrlExpired() {
        String id = "randomId";
        when(urlRecordServiceImpl.retrieveOriginalUrl(id)).thenThrow(new UrlExpiredException(id));

        assertThrows(UrlExpiredException.class, () -> urlServiceImpl.getOriginalUrl(id));
    }

    @Test
    public void shouldGenerate32charId() throws UrlProcessingException {
        // Given
        String longUrl = "http://www.example.com";
        int length = 32;
        int expirationHours = 24;
        String uuid32 = "4f5b5f7d364e481d8339b8a9681f0579";
        String baseUrl = "http://short.url/";
        Url mockUrl = Url.builder()
                .originalUrl(longUrl)
                .shortenedUrlId(uuid32)
                .shortenedBaseUrl(baseUrl)
                .build();

        // When
        when(utilsServiceImpl.generateRandomId(length)).thenReturn(uuid32);
        when(urlRecordServiceImpl.createUrlRecord(longUrl, baseUrl, uuid32, expirationHours)).thenReturn(mockUrl);
        when(utilsServiceImpl.generateShortUrl(baseUrl, uuid32)).thenReturn(baseUrl + uuid32);

        // Then
        String shortUrl = urlServiceImpl.shortenUrl(longUrl, expirationHours, length);
        assertNotNull(shortUrl);
        assertEquals(length, shortUrl.replace(baseUrl, "").length());
    }


}
