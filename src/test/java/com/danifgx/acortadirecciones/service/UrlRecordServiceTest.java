package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.UrlRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlRecordServiceTest {

    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlRecordService urlRecordService;

    @Test
    void testCreateUrlRecord() {
        Url url = new Url();
        String originalUrl = "https://www.example.com";
        String baseUrl = "http://short.url/";
        String id = "randomId1234";
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = urlRecordService.createUrlRecord(originalUrl, baseUrl, id, 2);

        assertEquals(baseUrl + id, result);
    }

    @Test
    void testRetrieveOriginalUrlSuccessful() {
        Url url = new Url();
        url.setOriginalUrl("https://www.example.com");
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

// ... otros tests
}