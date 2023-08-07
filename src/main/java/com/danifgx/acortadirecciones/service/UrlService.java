package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.entity.UrlLog;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.repository.UrlLogRepository;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlLogRepository urlLogRepository;

    public UrlService(UrlRepository urlRepository, UrlLogRepository urlLogRepository) {
        this.urlRepository = urlRepository;
        this.urlLogRepository = urlLogRepository;
    }

    public String shortenUrl(String originalUrl) {
        String id = generateRandomId();

        Url url = new Url(id, originalUrl, LocalDateTime.now(), LocalDateTime.now().plusHours(24));
        urlRepository.save(url);

        UrlLog urlLog = new UrlLog(id, originalUrl, "http://localhost:8080/" + id, LocalDateTime.now());
        urlLogRepository.save(urlLog);

        return id;
    }
    public String getOriginalUrl(String id) {
        Url url = urlRepository.findById(id).orElseThrow(() -> new UrlNotFoundException(id));
        if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
            urlRepository.deleteById(id);
            throw new UrlExpiredException(id);
        }
        return url.getOriginalUrl();
    }

    public void deleteExpiredUrls() {
        List<Url> expiredUrls = urlRepository.findByExpiryDateBefore(LocalDateTime.now());
        urlRepository.deleteAll(expiredUrls);
    }

    private String generateRandomId() {
        // Truncate to 50 characters if you want
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 50);
    }
}
