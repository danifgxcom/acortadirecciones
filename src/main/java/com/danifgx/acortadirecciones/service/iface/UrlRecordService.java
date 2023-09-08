package com.danifgx.acortadirecciones.service.iface;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;

public interface UrlRecordService {

    Url createUrlRecord(String originalUrl, String baseUrl, String id, int expirationHours) throws UrlProcessingException;

    String retrieveOriginalUrl(String shortenedUrlId);

    void purgeExpiredUrls();

}
