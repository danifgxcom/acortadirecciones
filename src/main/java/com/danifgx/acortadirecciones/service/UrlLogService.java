package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.entity.UrlLog;

import java.util.Optional;

public interface UrlLogService {

    public void logUrlCreation(Url url);
    public boolean isExistingShortenedId(String shortUrlId);
    public Optional<UrlLog> findByOriginalUrl(String originalUrl);
}