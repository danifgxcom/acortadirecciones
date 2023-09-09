package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.iface.UrlRecordService;
import com.danifgx.acortadirecciones.service.validation.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordServiceImpl implements UrlRecordService {

    private final UrlRepository urlRepository;
    private final MongoTemplate mongoTemplate;
    private final UrlValidator urlValidator;

    @Autowired
    public UrlRecordServiceImpl(UrlRepository urlRepository, MongoTemplate mongoTemplate, UrlValidator urlValidator) {
        this.urlRepository = urlRepository;
        this.mongoTemplate = mongoTemplate;
        this.urlValidator = urlValidator;
    }


    @Override
    public Url createUrlRecord(Url url) throws UrlProcessingException {
        if (urlValidator.validateUrl(url.getOriginalUrl()))
            return saveUrl(url);
        else {
            throw new UrlProcessingException("Unable to save URL");
        }
    }

    @Override
    public Url createUrlRecord(Url url, String collectionName) throws UrlProcessingException {
        if (urlValidator.validateUrl(url.getOriginalUrl())) {
            return mongoTemplate.save(url, collectionName);
        } else {
            throw new UrlProcessingException("Unable to save URL");
        }
    }

    public String retrieveOriginalUrl(String shortenedUrlId) throws UrlExpiredException, UrlNotFoundException {
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

    private Url saveUrl(Url url) {
        return urlRepository.save(url);
    }
}
