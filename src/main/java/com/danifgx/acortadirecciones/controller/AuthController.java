package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.AuthenticationService;
import com.danifgx.acortadirecciones.service.CookieService;
import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.impl.CookieServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final UtilsService utilsService;
    private final CookieService cookieService;
    private final ObjectMapper objectMapper;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService,
                          AuthenticationManager authenticationManager, UtilsService utilsService,
                          CookieServiceImpl cookieService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.utilsService = utilsService;
        this.cookieService = cookieService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/loggedIn")
    public RedirectView loggedHome(HttpServletRequest request, HttpServletResponse response) {
        OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String jwtToken = jwtService.generateToken(oidcUser);
        cookieService.setJwtCookie(response, jwtToken);
        cookieService.setUserDataCookie(response, oidcUser);
        return new RedirectView("http://localhost:3000/shorten");
    }

    @GetMapping("/logoutPage")
    public String showLogoutPage() {
        return "logout";
    }

    @GetMapping("/error")
    public String error() {
        log.error("An error page was accessed");
        return "error";
    }

    private void setCookies(HttpServletResponse response, OidcUser oidcUser, String jwtToken) {
        setJwtCookie(response, jwtToken);
        setUserDataCookie(response, collectUserData(oidcUser));
    }

    private void setJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        response.addCookie(jwtCookie);
    }

    private void setUserDataCookie(HttpServletResponse response, Map<String, Object> userData) {
        try {
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
        userData.put("role", collectRoles(oidcUser));
        userData.put("lastRequest", LocalDateTime.now().toString());
        return userData;
    }

    private String collectRoles(OidcUser oidcUser) {
        return oidcUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
    }
}
