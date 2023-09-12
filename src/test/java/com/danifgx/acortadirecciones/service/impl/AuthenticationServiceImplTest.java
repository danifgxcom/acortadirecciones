package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAuthenticateUser_Success() {
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("username", "password"))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(jwtService.generateToken(oidcUser)).thenReturn("someJwtToken");

        String token = authenticationServiceImpl.authenticateUser("username", "password");

        assertEquals("someJwtToken", token);
    }

    @Test
    public void testAuthenticateUser_Failure_AuthenticationException() {
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("username", "password")))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        assertThrows(AuthenticationException.class, () -> {
            authenticationServiceImpl.authenticateUser("username", "password");
        });
    }

    @Test
    public void testAuthenticateUser_Failure_NotAuthenticated() {
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("username", "password"))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> {
            authenticationServiceImpl.authenticateUser("username", "password");
        });
    }
}
