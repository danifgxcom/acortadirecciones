package com.danifgx.acortadirecciones.service;

import org.springframework.security.core.AuthenticationException;

public interface AuthenticationService {
    String authenticateUser(String username, String password) throws AuthenticationException;
}
