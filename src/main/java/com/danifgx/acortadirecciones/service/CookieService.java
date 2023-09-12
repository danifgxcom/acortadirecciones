package com.danifgx.acortadirecciones.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface CookieService {

    void setJwtCookie(HttpServletResponse response, String jwtToken);

    void setUserDataCookie(HttpServletResponse response, OidcUser oidcUser);
}
