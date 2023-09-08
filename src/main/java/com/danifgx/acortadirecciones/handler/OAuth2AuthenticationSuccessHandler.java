package com.danifgx.acortadirecciones.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

            if (token.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) token.getPrincipal();
                String email = oidcUser.getAttribute("email");

                // Crear y configurar cookie
                Cookie cookie = new Cookie("auth_token", email);  // Ajustar según tus necesidades
                cookie.setHttpOnly(true);
                cookie.setSecure(true);  // Habilitar solo si la conexión es segura (HTTPS)
                cookie.setPath("/"); // O especificar una ruta
                cookie.setMaxAge(7 * 24 * 60 * 60);  // Duración en segundos (opcional)

                // Agregar cookie a la respuesta
                response.addCookie(cookie);
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
