package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.service.UtilsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Value("${base.url}")
    public String baseUrl;


    @Override
    public String generateRandomId(int length) {
        if (length >= 0)
            return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
        else
            return "";
    }

    @Override
    public String generateShortUrl(String baseUrl, String id) {
        return baseUrl +id;
    }

    @Override
    public String generateShortUrl(String id) {
        return generateShortUrl(this.baseUrl, id);
    }
    @Override
    public String extractDomain(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        return url.getHost();
    }
}
