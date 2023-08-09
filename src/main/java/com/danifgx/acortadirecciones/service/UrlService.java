package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public class UrlService {

    private final IdGeneratorService idGeneratorService;
    private final UrlRecordService urlRecordService;
    private final UrlVerifierService urlVerifierService;
    @Value("${base.url}")
    private String baseUrl;

    public UrlService(
            IdGeneratorService idGeneratorService,
            UrlRecordService urlRecordService,
            UrlVerifierService urlVerifierService
    ) {
        this.idGeneratorService = idGeneratorService;
        this.urlRecordService = urlRecordService;
        this.urlVerifierService = urlVerifierService;
    }

    public String shortenUrl(String originalUrl, int expirationHours) throws UrlProcessingException {
        try {
            urlVerifierService.verifyUrl(originalUrl);

            String id = idGeneratorService.generateRandomId();
            return urlRecordService.createUrlRecord(originalUrl, baseUrl, id, expirationHours);
        } catch (UrlProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new UrlProcessingException("Error processing the URL: " + originalUrl, e);
        }
    }


    public String getOriginalUrl(String shortenedUrlId) {
        return urlRecordService.retrieveOriginalUrl(shortenedUrlId);
    }

    public void deleteExpiredUrls() {
        urlRecordService.purgeExpiredUrls();
    }
}
