package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.impl.AuthenticationService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    private final UtilsService utilsService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    AuthController(JwtService jwtService, AuthenticationService authenticationService, AuthenticationManager authenticationManager, UtilsService utilsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.utilsService = utilsService;
    }

    @GetMapping("/loggedIn")
    public RedirectView loggedHome(HttpServletRequest request, HttpServletResponse response) {
        OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String jwtToken = jwtService.generateToken(oidcUser);

        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        response.addCookie(jwtCookie);

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", oidcUser.getEmail());
        userData.put("username", oidcUser.getName());
        List<String> roles = oidcUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String rolesString = String.join(", ", roles);
        userData.put("role", rolesString);
        userData.put("lastRequest", LocalDateTime.now().toString());

        try {
            String jsonUserData = objectMapper.writeValueAsString(userData);
            String encodedJsonUserData = URLEncoder.encode(jsonUserData, StandardCharsets.UTF_8.toString()); // encode the JSON string
            Cookie userDataCookie = new Cookie("userData", encodedJsonUserData);
            userDataCookie.setHttpOnly(false);
            userDataCookie.setSecure(false); // Enable in production
            userDataCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(userDataCookie);
        } catch (JsonProcessingException e) {
            log.error("Exception found when using Google OIDC: {}", e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding exception: {}", e.getMessage());
        }

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
}
