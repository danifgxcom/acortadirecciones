package com.danifgx.acortadirecciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    @GetMapping("/login/oauth2")
    public String login() {
        return "login";
    }

    @GetMapping("/loggedIn")
    public String home() {
        return "homeAuthenticated";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
