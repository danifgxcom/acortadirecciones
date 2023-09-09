package com.danifgx.acortadirecciones.service.iface;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;

public interface UrlService {
        public String shortenUrl(String originalUrl, int expirationHours, int length) throws UrlProcessingException;

        public String getOriginalUrl(String shortenedUrlId);

        public void deleteExpiredUrls();

        public Url saveUrlRecord(Url url, String collectionName);


}