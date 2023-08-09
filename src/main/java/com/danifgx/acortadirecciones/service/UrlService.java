package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.entity.UrlLog;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.repository.UrlLogRepository;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.verification.GoogleSafeBrowsingVerifier;
import com.danifgx.acortadirecciones.service.verification.SimpleVerifier;
import com.danifgx.acortadirecciones.service.verification.VerificationChainHandler;
import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlLogRepository urlLogRepository;
    private final VerificationChainHandler verificationHandler;
    private final SimpleVerifier simpleVerifier;
    private final GoogleSafeBrowsingVerifier googleSafeBrowsingVerifier;

    @Value("${base.url}")
    private String baseUrl;

    public UrlService(
            UrlRepository urlRepository,
            UrlLogRepository urlLogRepository,
            VerificationChainHandler verificationHandler,
            SimpleVerifier simpleVerifier,
            GoogleSafeBrowsingVerifier googleSafeBrowsingVerifier
    ) {
        this.urlRepository = urlRepository;
        this.urlLogRepository = urlLogRepository;
        this.verificationHandler = verificationHandler;
        this.simpleVerifier = simpleVerifier;
        this.googleSafeBrowsingVerifier = googleSafeBrowsingVerifier;

        //this.verificationHandler.addVerifier(simpleVerifier);
        this.verificationHandler.addVerifier(googleSafeBrowsingVerifier);
    }

    public String shortenUrl(String originalUrl) {
        VerificationResponse response = verificationHandler.verifyUrl(originalUrl);
        if (!response.isSuccess()) {
            throw new IllegalArgumentException("¡URL no es segura o no está permitida! Rechazado por: " + response.getFailedVerifier());
        }

        Optional<UrlLog> existingLog = urlLogRepository.findByOriginalUrl(originalUrl);
        if (existingLog.isPresent()) {
            return existingLog.get().getId();
        }

        if (existingUrl(originalUrl)) {
            throw new IllegalArgumentException("La URL ya está acortada en nuestro sistema!");
        }

        String id = generateRandomId();

        Url url = new Url(id, originalUrl, LocalDateTime.now(), LocalDateTime.now().plusHours(24));
        urlRepository.save(url);

        UrlLog urlLog = new UrlLog(id, originalUrl, baseUrl + id, LocalDateTime.now());
        urlLogRepository.save(urlLog);

        return id;
    }

    private boolean existingUrl(String url) {
        String pattern = baseUrl + ".*";
        return url.matches(pattern);
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
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}
