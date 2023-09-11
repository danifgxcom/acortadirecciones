package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.service.UtilsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Override
    public void invalidateCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0); // Invalida la cookie estableciendo su tiempo máximo de edad en 0
                    cookie.setPath("/"); // Importante: asegúrate de que la ruta sea coherente con la ruta donde se estableció la cookie.
                    cookie.setHttpOnly(true); // Opcional, pero recomendado
                    cookie.setSecure(true); // Opcional, pero recomendado si la cookie es segura
                    response.addCookie(cookie); // Agrega la cookie a la respuesta para invalidarla en el cliente
                    break;
                }
            }
        }
    }
}
