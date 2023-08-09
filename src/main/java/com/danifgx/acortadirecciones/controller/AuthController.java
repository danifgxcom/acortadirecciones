package com.danifgx.acortadirecciones.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String loginOptions() {
        logger.info("Accessing login options");
        return "loginOptions";
    }

    @GetMapping("/loggedIn")
    public String home() {
        logger.info("Logged in successfully. Redirecting to home.");
        return "homeAuthenticated";
    }

    @GetMapping("/error")
    public String error() {
        logger.error("An error page was accessed");
        return "error";
    }
}
