package com.danifgx.acortadirecciones.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    @Value("${base.url}")
    private String baseUrl;
    private final IdGeneratorService idGeneratorService;
    private final UrlRecordService urlRecordService;
    private final UrlVerifierService urlVerifierService;

    public UrlService(
            IdGeneratorService idGeneratorService,
            UrlRecordService urlRecordService,
            UrlVerifierService urlVerifierService
    ) {
        this.idGeneratorService = idGeneratorService;
        this.urlRecordService = urlRecordService;
        this.urlVerifierService = urlVerifierService;
    }

    public String shortenUrl(String originalUrl, int expirationHours) {
        urlVerifierService.verifyUrl(originalUrl);

        String id = idGeneratorService.generateRandomId();
        return urlRecordService.createUrlRecord(originalUrl, baseUrl, id, expirationHours);
    }

    public String getOriginalUrl(String shortenedUrlId) {
        return urlRecordService.retrieveOriginalUrl(shortenedUrlId);
    }

    public void deleteExpiredUrls() {
        urlRecordService.purgeExpiredUrls();
    }
}
