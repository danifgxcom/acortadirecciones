package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.iface.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private JwtService jwtService;

    @Autowired
    AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public String loginOptions() {
        logger.info("Accessing login options");
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
        logger.error("An error page was accessed");
        return "error";
    }
}
