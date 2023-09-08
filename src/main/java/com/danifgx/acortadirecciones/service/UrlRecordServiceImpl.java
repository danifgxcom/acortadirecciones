package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.iface.UrlRecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordServiceImpl implements UrlRecordService {

    private final UrlRepository urlRepository;
    private final int maxUrlLength;

    public UrlRecordServiceImpl(UrlRepository urlRepository,
                                @Value("${url.max.length}") int maxUrlLength) {
        this.urlRepository = urlRepository;
        this.maxUrlLength = maxUrlLength;
    }


    @Override
    public Url createUrlRecord(String originalUrl, String baseUrl, String id, int expirationHours) throws UrlProcessingException {
        validateUrl(originalUrl);
        checkUrlLength(originalUrl);

        Url newUrl = buildNewUrl(originalUrl, baseUrl, id, expirationHours);
        return saveUrl(newUrl);
    }

    public String retrieveOriginalUrl(String shortenedUrlId) {
        Url url = urlRepository.findByShortenedUrlId(shortenedUrlId).orElseThrow(() -> new UrlNotFoundException(shortenedUrlId));
        if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
            urlRepository.deleteById(shortenedUrlId);
            throw new UrlExpiredException(shortenedUrlId);
        }
        return url.getOriginalUrl();
    }

    @Override
    public void purgeExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findByExpiryDateBefore(LocalDateTime.now());
        urlRepository.deleteAll(expiredUrls);
    }

    private Url buildNewUrl(String originalUrl, String baseUrl, String id, int expirationHours) {
        LocalDateTime now = LocalDateTime.now();
        return Url.builder()
                .originalUrl(originalUrl)
                .shortenedBaseUrl(baseUrl)
                .shortenedUrlId(id)
                .creationDate(now)
                .expiryDate(now.plusHours(expirationHours))
                .build();
    }

    private Url saveUrl(Url url) {
        return urlRepository.save(url);
    }

    private void checkUrlLength(String originalUrl) throws UrlProcessingException {
        if (originalUrl.length() > maxUrlLength) {
            throw new UrlProcessingException("URL exceeds the maximum allowed length");
        }
    }

    private void validateUrl(String originalUrl) throws UrlProcessingException {
        try {
            new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new UrlProcessingException("Invalid URL provided: " + originalUrl, e);
        }
    }

}
