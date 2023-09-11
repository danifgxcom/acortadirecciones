package com.danifgx.acortadirecciones.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.MalformedURLException;

public interface UtilsService {

    public String generateRandomId(int length);

    public String generateShortUrl(String baseUrl, String id);

    public String generateShortUrl(String id);

    public String extractDomain(String urlString) throws MalformedURLException;

    public void invalidateCookie(HttpServletRequest request, HttpServletResponse response, String cookieName);
}
