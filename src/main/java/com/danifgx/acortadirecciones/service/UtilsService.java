package com.danifgx.acortadirecciones.service;

import java.net.MalformedURLException;

public interface UtilsService {

    public String generateRandomId(int length);

    public String generateShortUrl(String baseUrl, String id);

    public String generateShortUrl(String id);

    public String extractDomain(String urlString) throws MalformedURLException;
}
