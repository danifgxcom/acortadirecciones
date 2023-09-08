package com.danifgx.acortadirecciones.service.iface;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface JwtService {
    String generateToken(OidcUser oidcUser);
    Boolean validateToken(String token, String username);
    UserDetails parseToken(String token);
    String extractUsername(String token);
}