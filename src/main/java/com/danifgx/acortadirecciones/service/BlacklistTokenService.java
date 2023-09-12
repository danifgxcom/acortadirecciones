package com.danifgx.acortadirecciones.service;

public interface BlacklistTokenService {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
    void removeFromBlacklist(String token);
    void purgeExpiredTokens();
}
