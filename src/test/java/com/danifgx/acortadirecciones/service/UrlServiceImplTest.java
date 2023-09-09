package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.iface.UrlLogService;
import com.danifgx.acortadirecciones.service.validation.UrlValidatorImpl;
import com.danifgx.acortadirecciones.service.verification.UrlVerifierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.MalformedURLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @InjectMocks
    private UrlServiceImpl urlServiceImpl;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;
    @Mock
    private UrlRecordServiceImpl urlRecordServiceImpl;
    @Mock
    private UrlVerifierServiceImpl urlVerifierServiceImpl;
    @Mock
    private UrlLogService urlLogService;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private UrlValidatorImpl urlValidatorImpl;
    @Mock
    private UrlRepository urlRepository;

    @BeforeEach
    public void setUp() {
        urlServiceImpl.setBaseUrl("http://short.url/");
    }

    @Test
    void testShortenUrlSuccessfully() throws MalformedURLException, UrlProcessingException {
        String originalUrl = "http://example.com";
        int expirationHours = 24;
        int length = 32;
        String id = "randomId";
        Url mockUrl = mock(Url.class);
        UrlRepository mockUrlRepository = mock(UrlRepository.class);

        when(utilsServiceImpl.generateRandomId(length)).thenReturn(id);
        when(urlValidatorImpl.validateUrl(anyString())).thenReturn(true);
        when(urlRecordServiceImpl.createUrlRecord(any())).thenReturn(mockUrl);
        when(utilsServiceImpl.generateShortUrl(anyString(), anyString())).thenReturn("http://short.url/" + id);

        doNothing().when(urlVerifierServiceImpl).verifyUrl(anyString());

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

        when(urlValidatorImpl.validateUrl(originalUrl)).thenReturn(true);
        doNothing().when(urlVerifierServiceImpl).verifyUrl(originalUrl);

        when(utilsServiceImpl.generateRandomId(length)).thenReturn(id);
        when(urlRecordServiceImpl.createUrlRecord(any()))
                .thenThrow(new UrlProcessingException("Simulated exception"));

        try {
            String result = urlServiceImpl.shortenUrl(originalUrl, expirationHours, length);
            fail("Expected UrlProcessingException to be thrown");
        } catch (UrlProcessingException e) {
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

        when(urlRecordServiceImpl.retrieveOriginalUrl(any())).thenThrow(new UrlNotFoundException("URL not found"));


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

        when(utilsServiceImpl.generateRandomId(length)).thenReturn(uuid32);
        when(urlRecordServiceImpl.createUrlRecord(any())).thenReturn(mockUrl);
        when(urlValidatorImpl.validateUrl(anyString())).thenReturn(true);
        when(utilsServiceImpl.generateShortUrl(baseUrl, uuid32)).thenReturn(baseUrl + uuid32);

        String shortUrl = urlServiceImpl.shortenUrl(longUrl, expirationHours, length);
        assertNotNull(shortUrl);
        assertEquals(length, shortUrl.replace(baseUrl, "").length());
    }

    @Test
    void testCreateAndSaveUrlWithDynamicCollection() {
        // Given
        String originalUrl = "https://www.example.com";
        String baseUrl = "http://short.url/";
        String id = "randomId1234";
        String collectionName = "urls_16";

        Url urlToBeSaved = Url.builder()
                .id(id)
                .originalUrl(originalUrl)
                .shortenedBaseUrl(baseUrl)
                .shortenedUrlId("randomId")
                .creationDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusHours(2))
                .build();

        when(urlRecordServiceImpl.createUrlRecord(urlToBeSaved, collectionName)).thenReturn(urlToBeSaved);

        // When
        Url result = urlServiceImpl.saveUrlRecord(urlToBeSaved, collectionName);

        // Then
        assertNotNull(result);
        assertEquals(urlToBeSaved, result);
        verify(urlRecordServiceImpl, times(1)).createUrlRecord(urlToBeSaved, collectionName);
    }

}
