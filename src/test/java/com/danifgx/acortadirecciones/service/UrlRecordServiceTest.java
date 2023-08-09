package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRecordServiceTest {

    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlRecordService urlRecordService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlRecordService, "maxUrlLength", 500);
    }

    @Test
    void testCreateUrlRecord() {
        String originalUrl = "https://www.example.com";
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        String baseUrl = "http://short.url/";
        String id = "randomId1234";

        when(urlRepository.save(any(Url.class))).thenReturn(url);

        assertDoesNotThrow(() -> {
            String result = urlRecordService.createUrlRecord(originalUrl, baseUrl, id, 2);
            assertEquals(baseUrl + id, result);
        });
    }


    @Test
    void testRetrieveOriginalUrlSuccessful() {
        Url mockUrl = Url.builder()
                .id("id")
                .shortenedBaseUrl("http://example.com")
                .shortenedUrlId("shortUrlId")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .creationDate(LocalDateTime.now())
                .originalUrl("https://www.example.com").build();

        when(urlRepository.findByShortenedUrlId(anyString())).thenReturn(Optional.of(mockUrl));

        String result = urlRecordService.retrieveOriginalUrl("randomId1234");

        assertEquals("https://www.example.com", result);
    }

    @Test
    void testRetrieveOriginalUrlNotFound() {
        when(urlRepository.findByShortenedUrlId("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlRecordService.retrieveOriginalUrl("nonExistentId"));
    }

    @Test
    void testRetrieveExpiredUrl() {
        Url url = new Url();
        url.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(urlRepository.findByShortenedUrlId("expiredId")).thenReturn(Optional.of(url));

        assertThrows(UrlExpiredException.class, () -> urlRecordService.retrieveOriginalUrl("expiredId"));
    }

    @Test
    void testDatabaseErrorOnUrlCreation() {
        String validUrl = "https://www.example.com";
        String baseUrl = "http://short.url/";
        String id = "randomId1234";

        when(urlRepository.save(any(Url.class))).thenThrow(new DataAccessException("Database connection error") {
        });

        assertThrows(DataAccessException.class, () -> urlRecordService.createUrlRecord(validUrl, baseUrl, id, 2));
    }

    @Test
    void testDatabaseErrorOnRetrieveOriginalUrl() {
        when(urlRepository.findByShortenedUrlId(anyString())).thenThrow(new DataAccessException("Database connection error") {
        });

        assertThrows(DataAccessException.class, () -> urlRecordService.retrieveOriginalUrl("randomId1234"));
    }

    @Test
    void testInvalidUrlOnCreation() {
        String invalidUrl = "invalid-url";
        String baseUrl = "http://short.url/";
        String id = "randomId1234";

        assertThrows(UrlProcessingException.class, () -> urlRecordService.createUrlRecord(invalidUrl, baseUrl, id, 2));
    }

    @Test
    void testUrlExceedsMaxLengthOnCreation() {
        String tooLongUrl = "http://example.com/" + "a".repeat(2000);  // Asumiendo que 2000 es mayor que tu longitud máxima permitida
        String baseUrl = "http://short.url/";
        String id = "randomId1234";

        assertThrows(UrlProcessingException.class, () -> urlRecordService.createUrlRecord(tooLongUrl, baseUrl, id, 2));
    }
    @Test
    void testPurgeExpiredUrls() {
        Url expiredUrl = Url.builder()
                .id("expiredId")
                .expiryDate(LocalDateTime.now().minusHours(1))
                .creationDate(LocalDateTime.now().minusHours(2))
                .originalUrl("https://www.expired.com").build();

        Url validUrl = Url.builder()
                .id("validId")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .creationDate(LocalDateTime.now())
                .originalUrl("https://www.valid.com").build();

        urlRecordService.purgeExpiredUrls();
        verify(urlRepository).deleteAll(anyList());
        verify(urlRepository, times(0)).delete(validUrl);
    }


}
