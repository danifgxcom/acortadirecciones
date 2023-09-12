package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.entity.UrlLog;
import com.danifgx.acortadirecciones.persistence.dao.UrlLogDao;
import com.danifgx.acortadirecciones.service.UrlLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlLogServiceImpl implements UrlLogService {

    private final UrlLogDao urlLogDao;

    @Autowired
    public UrlLogServiceImpl(UrlLogDao urlLogDao) {
        this.urlLogDao = urlLogDao;
    }

    @Override
    public void logUrlCreation(Url url) {
        LocalDateTime now = LocalDateTime.now();
        UrlLog urlLog = UrlLog.builder()
                .originalUrl(url.getOriginalUrl())
                .shortenedBaseUrl(url.getShortenedBaseUrl())
                .shortenedUrlId(url.getId())
                .creationDate(url.getCreationDate())
                .expiryDate(url.getExpiryDate())
                .createdAt(now)
                .createdBy(this.getClass().toString())
                .build();

        this.urlLogDao.save(urlLog);
    }

    @Override
    public boolean isExistingShortenedId(String shortUrlId) {
        Optional<UrlLog> urlLog = urlLogDao.findByShortenedUrlId(shortUrlId);
        return urlLog.isPresent();
    }

    @Override
    public Optional<UrlLog> findByOriginalUrl(String originalUrl) {
        return urlLogDao.findByOriginalUrl(originalUrl);
    }
}
