package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.service.UrlLogService;
import com.danifgx.acortadirecciones.service.UrlRecordService;
import com.danifgx.acortadirecciones.service.UrlService;
import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.validation.iface.UrlValidator;
import com.danifgx.acortadirecciones.service.verification.UrlVerifierServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UtilsService utilsService;
    private final UrlRecordService urlRecordServiceImpl;
    private final UrlVerifierServiceImpl urlVerifierServiceImpl;
    private final UrlLogService urlLogService;
    private final UrlValidator urlValidator;
    @Value("${base.url}")
    @Setter
    private String baseUrl;

    @Transactional
    public String shortenUrl(String originalUrl, int expirationHours, int length){

        validateAndVerifyUrl(originalUrl);

        String id = generateUniqueId(length);
        createAndLogNewUrlRecord(originalUrl, id, expirationHours);

        return utilsService.generateShortUrl(baseUrl, id);
    }


    public String getOriginalUrl(String shortenedUrlId) {
        return urlRecordServiceImpl.retrieveOriginalUrl(shortenedUrlId);
    }

    public void deleteExpiredUrls() {
        urlRecordServiceImpl.purgeExpiredUrls();
    }
    private String generateUniqueId(int length) throws UrlProcessingException {
        String id;
        int collisions = 0;
        do {
            id = utilsService.generateRandomId(length);
            collisions++;
        }
        while (urlLogService.isExistingShortenedId(id) && collisions <= 10);

        if (collisions > 10) {
            log.warn(collisions +  " means low performance / collection filled");
            throw new UrlProcessingException("Performance is degrading");
        }
        return id;
    }
    @Override
    public Url saveUrlRecord(Url url, String collectionName) {
        return urlRecordServiceImpl.createUrlRecord(url, collectionName);
    }


    private void validateAndVerifyUrl(String url) {
        if (!urlValidator.validateUrl(url)) {
            throw new UrlProcessingException("The URL is malformed");
        }

        urlVerifierServiceImpl.verifyUrl(url);
    }

    private void createAndLogNewUrlRecord(String originalUrl, String id, int expirationHours) {
        Url url = buildNewUrl(originalUrl, baseUrl, id, expirationHours);
        Url newUrlRecord = urlRecordServiceImpl.createUrlRecord(url);
        log.debug("Repository insertion: {}", newUrlRecord);
        urlLogService.logUrlCreation(newUrlRecord);
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
}
