package com.danifgx.acortadirecciones.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static org.mockito.Mockito.*;

public class CookieServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private OidcUser oidcUser;

    private CookieServiceImpl cookieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cookieService = new CookieServiceImpl(objectMapper);
    }

    @Test
    void testSetJwtCookie() {
        String jwtToken = "dummyJwtToken";

        cookieService.setJwtCookie(httpServletResponse, jwtToken);

        verify(httpServletResponse).addCookie(argThat(cookie -> "jwt_token".equals(cookie.getName()) && jwtToken.equals(cookie.getValue())));
    }

    @Test
    void testSetUserDataCookie() throws JsonProcessingException, UnsupportedEncodingException {
        when(oidcUser.getEmail()).thenReturn("testEmail");
        when(oidcUser.getName()).thenReturn("testName");

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        doReturn(Collections.singletonList(authority)).when(oidcUser).getAuthorities();


        when(objectMapper.writeValueAsString(any())).thenReturn("jsonString");

        cookieService.setUserDataCookie(httpServletResponse, oidcUser);

        verify(httpServletResponse).addCookie(any());
    }
}
