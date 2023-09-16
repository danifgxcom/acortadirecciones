package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class OptionsControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private OptionsController optionsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        optionsController = new OptionsController(userService);
    }

    private void setupAuthenticationWithRoles(String... roles) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        doReturn(grantedAuthorities).when(authentication).getAuthorities();
    }

    @Test
    public void testAvailableLengthsForGoogleRole() {
        setupAuthenticationWithRoles("OIDC_USER");

        when(authentication.getDetails()).thenReturn("24,32");

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(24, 32), result);
    }

    @Test
    public void testAvailableLengthsForPremiumRole() {
        setupAuthenticationWithRoles("PREMIUM");

        when(authentication.getDetails()).thenReturn("16,24,32");

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(16, 24, 32), result);
    }

    @Test
    public void testAvailableLengthsForLifetimeRole() {
        setupAuthenticationWithRoles("LIFETIME");

        when(authentication.getDetails()).thenReturn("8,16,24,32");

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(8, 16, 24, 32), result);
    }

    @Test
    public void testAvailableLengthsForAnonymousRole() {
        setupAuthenticationWithRoles("ANONYMOUS");

        when(authentication.getDetails()).thenReturn("32");

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(32), result);
    }
}
