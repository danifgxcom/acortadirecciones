package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.service.CookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CookieServiceImpl implements CookieService {

    private final ObjectMapper objectMapper;

    public CookieServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void setJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        response.addCookie(jwtCookie);
    }
    @Override
    public void setUserDataCookie(HttpServletResponse response, OidcUser oidcUser) {
        try {
            Map<String, Object> userData = collectUserData(oidcUser);
            String jsonUserData = objectMapper.writeValueAsString(userData);
            String encodedJsonUserData = URLEncoder.encode(jsonUserData, StandardCharsets.UTF_8.toString());
            Cookie userDataCookie = new Cookie("userData", encodedJsonUserData);
            userDataCookie.setHttpOnly(false);
            userDataCookie.setSecure(false);
            userDataCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(userDataCookie);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            log.error("Exception: {}", e.getMessage());
        }
    }

    private Map<String, Object> collectUserData(OidcUser oidcUser) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", oidcUser.getEmail());
        userData.put("username", oidcUser.getName());
        userData.put("role", oidcUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
        userData.put("lastRequest", LocalDateTime.now().toString());
        return userData;
    }
}
