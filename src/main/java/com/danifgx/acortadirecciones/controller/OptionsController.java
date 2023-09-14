package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.security.profile.Permission;
import com.danifgx.acortadirecciones.security.profile.PermissionValueMap;
import com.danifgx.acortadirecciones.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
            Set<Permission> userPermissions = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(String::valueOf)
                    .map(Permission::valueOf)
                    .collect(Collectors.toSet());

            int permissionLevel = 0;

            if (userPermissions.containsAll(PermissionValueMap.LENGTH_OPTION_8.permissions)) {
                permissionLevel = 8;
            } else if (userPermissions.containsAll(PermissionValueMap.LENGTH_OPTION_16.permissions)) {
                permissionLevel = 16;
            } else if (userPermissions.containsAll(PermissionValueMap.LENGTH_OPTION_24.permissions)) {
                permissionLevel = 24;
            }

            switch (permissionLevel) {
                case 8:
                    availableLengths.add(8);
                case 16:
                    availableLengths.add(16);
                case 24:
                    availableLengths.add(24);
            }
        }

        availableLengths.add(32);
        return availableLengths;
    }
}

