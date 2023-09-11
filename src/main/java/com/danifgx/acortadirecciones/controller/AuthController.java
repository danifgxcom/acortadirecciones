package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.impl.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;


    AuthController(JwtService jwtService, AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }


    @GetMapping("/login")
    public String loginOptions() {
        log.info("Accessing login options");
        return "loginOptions";
    }

    @GetMapping("/loggedIn")
    public RedirectView loggedHome(HttpServletRequest request, HttpServletResponse response) {
        OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String jwtToken = jwtService.generateToken(oidcUser);

        Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
        response.addCookie(jwtCookie);

        // I avoid extra logic at the front-end to decode the JWT to present the username (email) in the header
        Cookie emailCookie = new Cookie("userEmail", oidcUser.getEmail());
        response.addCookie(emailCookie);


        return new RedirectView("http://localhost:3000/shorten");
    }




    @GetMapping("/error")
    public String error() {
        log.error("An error page was accessed");
        return "error";
    }
}
