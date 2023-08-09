package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordService {

    private final UrlRepository urlRepository;
    @Value("${base.url}")
    private String baseUrl;
    @Value("${url.max.length}")
    private int maxUrlLength;

    public UrlRecordService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String createUrlRecord(String originalUrl, String baseUrl, String id, int expirationHours) throws UrlProcessingException {

        validateUrl(originalUrl);

        if(originalUrl.length() > maxUrlLength) {
            throw new UrlProcessingException("URL exceeds the maximum allowed length");
        }


        LocalDateTime now = LocalDateTime.now();
        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortenedBaseUrl(baseUrl)
                .shortenedUrlId(id)
                .creationDate(now)
                .expiryDate(now.plusHours(expirationHours))
                .build();

        Url savedUrl = urlRepository.save(url);
        return baseUrl + id;
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

    private void validateUrl(String originalUrl) throws UrlProcessingException {
        try {
            new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new UrlProcessingException("Invalid URL provided: " + originalUrl, e);
        }
    }

}
