package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.service.iface.UrlLogService;
import com.danifgx.acortadirecciones.service.iface.UrlService;
import com.danifgx.acortadirecciones.service.iface.UtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UtilsService utilsService;
    private final UrlRecordServiceImpl urlRecordServiceImpl;
    private final UrlVerifierService urlVerifierService;
    private final UrlLogService urlLogService;
    @Value("${base.url}")
    private String baseUrl;

    @Transactional
    public String shortenUrl(String originalUrl, int expirationHours, int length) throws UrlProcessingException {
        try {
            urlVerifierService.verifyUrl(originalUrl);

            String id = generateUniqueId(length);
            createAndLogNewUrlRecord(originalUrl, id, expirationHours);

            return utilsService.generateShortUrl(baseUrl, id);
        } catch (UrlProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new UrlProcessingException("Error processing the URL: " + originalUrl, e);
        }
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

    private void createAndLogNewUrlRecord(String originalUrl, String id, int expirationHours) {
        Url newUrlRecord = urlRecordServiceImpl.createUrlRecord(originalUrl, baseUrl, id, expirationHours);
        log.debug("Repository insertion: {}", newUrlRecord);
        urlLogService.logUrlCreation(newUrlRecord);
    }
}
