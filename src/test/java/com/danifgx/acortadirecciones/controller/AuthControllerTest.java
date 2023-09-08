package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.iface.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private Authentication authentication;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(authentication.getPrincipal()).thenReturn(oidcUser);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        authController = new AuthController(jwtService);
    }


    @Test
    public void testLoginOptions() {
        String expectedViewName = "loginOptions";
        String actualViewName = authController.loginOptions();
        assertEquals(expectedViewName, actualViewName);
    }

    @Test
    public void testHome() {
        String jwtToken = "someJwtToken";
        when(oidcUser.getName()).thenReturn("username@mydomain.com");
        SecurityContextHolder.getContext().setAuthentication(new MockAuthentication(oidcUser));
        when(jwtService.generateToken(oidcUser)).thenReturn(jwtToken);
        String expectedRedirectUrl = "http://localhost:3000/shorten";
        String actualRedirectUrl = authController.loggedHome(request, response).getUrl();
        assertEquals(expectedRedirectUrl, actualRedirectUrl);
    }


    @Test
    public void testCookie() {

        OidcUser mockOidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(mockOidcUser);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(mockOidcUser);

        String jwtToken = "dummyJwtToken";
        when(jwtService.generateToken(mockOidcUser)).thenReturn(jwtToken);

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        RedirectView redirectView = authController.loggedHome(mock(HttpServletRequest.class), mockResponse);

        verify(mockResponse, times(1)).addCookie(argThat(cookie -> "jwt_token".equals(cookie.getName()) && jwtToken.equals(cookie.getValue())));

        assertEquals("http://localhost:3000/shorten", redirectView.getUrl());
    }
    @Test
    public void testError() {
        String expectedViewName = "error";
        String actualViewName = authController.error();
        assertEquals(expectedViewName, actualViewName);
    }

    public static class MockAuthentication extends org.springframework.security.authentication.AbstractAuthenticationToken {
        public MockAuthentication(OidcUser oidcUser) {
            super(oidcUser.getAuthorities());
            setDetails(oidcUser);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return getDetails();
        }
    }
}
