package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;
import com.danifgx.acortadirecciones.persistence.dao.UrlDao;
import com.danifgx.acortadirecciones.service.UrlRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlRecordServiceImpl implements UrlRecordService {

    private final UrlDao urlDao;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UrlRecordServiceImpl(UrlDao urlDao, MongoTemplate mongoTemplate) {
        this.urlDao = urlDao;
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
        Url url = urlDao.findByShortenedUrlId(shortenedUrlId).orElseThrow(() -> new UrlNotFoundException(shortenedUrlId));
        if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
            urlDao.deleteById(shortenedUrlId);
            throw new UrlExpiredException(shortenedUrlId);
        }
        return url.getOriginalUrl();
    }

    @Override
    public void purgeExpiredUrls() {
        List<Url> expiredUrls = urlDao.findByExpiryDateBefore(LocalDateTime.now());
        urlDao.deleteAll(expiredUrls);
    }

    private Url saveUrl(Url url) {
        return urlDao.save(url);
    }
}
