package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.dto.request.LoginRequest;
import com.danifgx.acortadirecciones.entity.CustomUserDetails;
import com.danifgx.acortadirecciones.exception.CustomAuthenticationException;
import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.impl.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
public class AdminLoginViewController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;


    AdminLoginViewController(JwtService jwtService, AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/admin/login")
    public String loginView() {
        return "loginOptions"; // nombre de la vista de Thymeleaf que contiene el formulario
    }

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@Valid @RequestBody LoginRequest request) {
        log.info("Attempting to authenticate admin: " + request.getUsername());

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (isUserAdmin(userDetails)) {
                String token = jwtService.generateToken(userDetails);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized as admin");
            }
        } catch (CustomAuthenticationException e) {
            log.error("Authentication failed for admin: " + request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin credentials");
        }
    }

    private boolean isUserAdmin(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            return customUserDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
        }
        return false;
    }
}
