package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/option")
@Slf4j
public class OptionsController {

    private final UserService userService;

    public OptionsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/available-lengths")
    public List<Integer> getAvailableLengths(Authentication authentication) {
        log.debug("Entrando en getAvailableLengths");
        List<Integer> availableLengths = new ArrayList<>();

        if (authentication != null) {
            String urlLengthsStr = (String) authentication.getDetails();

            if (urlLengthsStr != null) {
                availableLengths = List.of(urlLengthsStr.split(","))
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
        }

        return availableLengths;
    }
}


