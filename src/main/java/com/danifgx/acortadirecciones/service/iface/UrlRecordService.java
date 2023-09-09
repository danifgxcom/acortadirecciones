package com.danifgx.acortadirecciones.service.iface;

import com.danifgx.acortadirecciones.entity.Url;
import com.danifgx.acortadirecciones.exception.UrlExpiredException;
import com.danifgx.acortadirecciones.exception.UrlNotFoundException;
import com.danifgx.acortadirecciones.exception.UrlProcessingException;

public interface UrlRecordService {

    Url createUrlRecord(Url url) throws UrlProcessingException;

    public Url createUrlRecord(Url url, String collectionName);

    public String retrieveOriginalUrl(String shortenedUrlId) throws UrlExpiredException, UrlNotFoundException;

    void purgeExpiredUrls();



}
