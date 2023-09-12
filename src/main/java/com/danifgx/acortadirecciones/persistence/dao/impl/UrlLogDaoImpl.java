package com.danifgx.acortadirecciones.persistence.dao.impl;

import com.danifgx.acortadirecciones.entity.UrlLog;
import com.danifgx.acortadirecciones.persistence.dao.UrlLogDao;
import com.danifgx.acortadirecciones.persistence.repository.UrlLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlLogDaoImpl implements UrlLogDao {

    private final UrlLogRepository urlLogRepository;

    @Autowired
    public UrlLogDaoImpl(UrlLogRepository urlLogRepository) {
        this.urlLogRepository = urlLogRepository;
    }

    @Override
    public void save(UrlLog urlLog) {
        urlLogRepository.save(urlLog);
    }

    @Override
    public Optional<UrlLog> findByShortenedUrlId(String shortUrlId) {
        return urlLogRepository.findByShortenedUrlId(shortUrlId);
    }

    @Override
    public Optional<UrlLog> findByOriginalUrl(String originalUrl) {
        return urlLogRepository.findByOriginalUrl(originalUrl);
    }
}
