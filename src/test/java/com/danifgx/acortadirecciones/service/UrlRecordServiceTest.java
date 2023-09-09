package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.validation.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlRecordServiceTest {

    @Mock
    UrlRepository urlRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    UrlValidator urlValidator;

    UrlRecordServiceImpl urlRecordServiceImpl;

    @BeforeEach
    void setUp() {
        urlRecordServiceImpl = new UrlRecordServiceImpl(urlRepository, mongoTemplate, urlValidator);
    }


    @Test
    void testCreateUrlRecord() {
        String originalUrl = "https://www.example.com";
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        String baseUrl = "http://short.url/";
        String id = "randomId1234";

        when(urlValidator.validateUrl(anyString())).thenReturn(true);
        when(urlRepository.save(any(Url.class))).thenReturn(url);


        Url finalUrl = Url.builder()
                .id("someId")
                .originalUrl("http://example.com")
                .shortenedBaseUrl("http://short.url/")
                .shortenedUrlId("randomId")
                .creationDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusHours(2))
                .build();

        assertDoesNotThrow(() -> {
            Url result = urlRecordServiceImpl.createUrlRecord(finalUrl);
            assertEquals(url, result);
        });
    }


    @Test
    void testRetrieveOriginalUrlSuccessful() {
        Url mockUrl = Url.builder()
                .shortenedUrlId("randomId1234")
                .shortenedBaseUrl("http://example.com")
                .shortenedUrlId("shortUrlId")
                .creationDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusHours(2))
                .originalUrl("https://www.example.com")
                .build();

        when(urlRepository.findByShortenedUrlId(anyString())).thenReturn(Optional.of(mockUrl));

        String result = urlRecordServiceImpl.retrieveOriginalUrl("randomId1234");

        assertEquals("https://www.example.com", result);
    }

    @Test
    void testRetrieveOriginalUrlNotFound() {
        when(urlRepository.findByShortenedUrlId("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlRecordServiceImpl.retrieveOriginalUrl("nonExistentId"));
    }

    @Test
    void testRetrieveExpiredUrl() {
        Url url = new Url();
        url.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(urlRepository.findByShortenedUrlId("expiredId")).thenReturn(Optional.of(url));

        assertThrows(UrlExpiredException.class, () -> urlRecordServiceImpl.retrieveOriginalUrl("expiredId"));
    }

    @Test
    void testDatabaseErrorOnUrlCreation() {

        Url validUrl = Url.builder()
                .originalUrl("http://example.com")
                .shortenedBaseUrl("http://short.url/")
                .shortenedUrlId("randomId1234")
                .build();

        when(urlValidator.validateUrl(any())).thenReturn(true);
        when(urlRepository.save(any(Url.class))).thenThrow(new DataAccessException("Database connection error") {
        });

        assertThrows(DataAccessException.class, () -> urlRecordServiceImpl.createUrlRecord(validUrl));
    }

    @Test
    void testDatabaseErrorOnRetrieveOriginalUrl() {
        when(urlRepository.findByShortenedUrlId(anyString())).thenThrow(new DataAccessException("Database connection error") {
        });

        assertThrows(DataAccessException.class, () -> urlRecordServiceImpl.retrieveOriginalUrl("randomId1234"));
    }

    @Test
    void testInvalidUrlOnCreation() {

        Url invalidUrl = Url.builder()
                .originalUrl("invalid-url")
                .shortenedBaseUrl("http://short.url/")
                .shortenedUrlId("randomId1234")
                .build();

        assertThrows(UrlProcessingException.class, () -> urlRecordServiceImpl.createUrlRecord(invalidUrl));
    }

    @Test
    void testUrlExceedsMaxLengthOnCreation() {
        Url tooLongUrl = Url.builder()
                .originalUrl("http://example.com/" + "a".repeat(2000))
                .shortenedBaseUrl("http://short.url/")
                .shortenedUrlId("randomId1234")
                .build();

        assertThrows(UrlProcessingException.class, () -> urlRecordServiceImpl.createUrlRecord(tooLongUrl));
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

        urlRecordServiceImpl.purgeExpiredUrls();
        verify(urlRepository).deleteAll(anyList());
        verify(urlRepository, times(0)).delete(validUrl);
    }


}
