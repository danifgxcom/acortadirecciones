package com.danifgx.acortadirecciones.service.iface;

public interface UtilsService {

    public String generateRandomId(int length);

    public String generateShortUrl(String baseUrl, String id);

    public String generateShortUrl(String id);
}
