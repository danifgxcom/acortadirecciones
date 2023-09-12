package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.persistence.repository.UrlRepository;
import com.danifgx.acortadirecciones.service.UrlRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordServiceImpl implements UrlRecordService {

    private final UrlRepository urlRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UrlRecordServiceImpl(UrlRepository urlRepository, MongoTemplate mongoTemplate) {
        this.urlRepository = urlRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Url createUrlRecord(Url url) throws UrlProcessingException {
        return saveUrl(url);
    }

    @Override
    public Url createUrlRecord(Url url, String collectionName) throws UrlProcessingException {
        return mongoTemplate.save(url, collectionName);
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
