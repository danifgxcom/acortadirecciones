package com.danifgx.acortadirecciones.service.iface;

import com.danifgx.acortadirecciones.entity.Url;

public interface UrlLogService {

    public void logUrlCreation(Url url);
    public boolean isExistingShortenedId(String shortUrlId);
}