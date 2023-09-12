package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.AuthenticationService;
import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.impl.CookieServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerTest.class);

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UtilsService utilsService;
    @Mock
    private CookieServiceImpl cookieService;
    @Mock
    private OidcUser oidcUser;
    @Mock
    private Authentication authentication;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ObjectMapper objectMapper;

    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setupSecurityContext();
        setupAuthenticationService();
        setupMockOidcUser();

        authController = new AuthController(jwtService, authenticationService, authenticationManager, utilsService, cookieService, objectMapper);
    }

    private void setupSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupAuthenticationService() {
        when(authenticationService.authenticateUser(anyString(), anyString())).thenReturn("someToken");
    }


    private String mockUserData() {
        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("email", "test@email.com");
        userDataMap.put("username", "testUsername");
        userDataMap.put("role", "ROLE_USER");
        userDataMap.put("lastRequest", LocalDateTime.now().toString());

        String jsonUserData = null;
        try {
            jsonUserData = objectMapper.writeValueAsString(userDataMap);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return jsonUserData;
    }

    private void setupMockOidcUser() {
        when(oidcUser.getEmail()).thenReturn("test@email.com");
        when(oidcUser.getName()).thenReturn("testUsername");
    }

    @Test
    public void testHome() {
        setupMockOidcUser();
        when(authentication.getPrincipal()).thenReturn(oidcUser);

        RedirectView redirectView = authController.loggedHome(request, response);
        assertEquals("http://localhost:3000/shorten", redirectView.getUrl());
    }


    @Test
    public void testCookie() {
        when(authentication.getPrincipal()).thenReturn(oidcUser);

        String jwtToken = "dummyJwtToken";
        when(jwtService.generateToken(oidcUser)).thenReturn(jwtToken);

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        RedirectView redirectView = authController.loggedHome(mock(HttpServletRequest.class), mockResponse);

        //verify(mockResponse, times(1)).addCookie(argThat(cookie -> "jwt_token".equals(cookie.getName()) && jwtToken.equals(cookie.getValue())));

        assertEquals("http://localhost:3000/shorten", redirectView.getUrl());
    }

    @Test
    public void testError() {
        String expectedViewName = "error";
        String actualViewName = authController.error();
        assertEquals(expectedViewName, actualViewName);
    }
}
