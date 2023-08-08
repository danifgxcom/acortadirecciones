package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginOptions() {
        return "loginOptions";
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