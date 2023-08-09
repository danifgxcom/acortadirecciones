package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordService {

    @Value("${base.url}")
    private String baseUrl;
    private final UrlRepository urlRepository;

    public UrlRecordService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String createUrlRecord(String originalUrl, String baseUrl, String id, int expirationHours) {
        LocalDateTime now = LocalDateTime.now();
        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortenedBaseUrl(baseUrl)
                .shortenedUrlId(id)
                .creationDate(now)
                .expiryDate(now.plusHours(expirationHours))
                .build();


        Url savedUrl = urlRepository.save(url);
        return baseUrl+id;
    }

    public String retrieveOriginalUrl(String shortenedUrlId) {
        Url url = urlRepository.findByShortenedUrlId(shortenedUrlId).orElseThrow(() -> new UrlNotFoundException(shortenedUrlId));
        if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
            urlRepository.deleteById(shortenedUrlId);
            throw new UrlExpiredException(shortenedUrlId);
        }
        return url.getOriginalUrl();
    }

    public void purgeExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findByExpiryDateBefore(LocalDateTime.now());
        urlRepository.deleteAll(expiredUrls);
    }
}
