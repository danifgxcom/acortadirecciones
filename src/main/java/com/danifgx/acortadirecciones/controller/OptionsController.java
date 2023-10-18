package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.service.UserService;
import com.danifgx.acortadirecciones.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            String userId = user.getId();
            return userService.getUserUrlLengths(userId);
        }
        return new ArrayList<>();
    }
    @GetMapping("/available-expirations")
    public List<Integer> getAvailableExpirationTimes(Authentication authentication) {
        LoggingUtil.logCurrentMethod(log);

        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            String userId = user.getId();
            return userService.getUserExpirationTimes(userId);
        }
        return new ArrayList<>();
    }
}


