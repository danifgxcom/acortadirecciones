package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.security.profile.Permission;
import com.danifgx.acortadirecciones.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
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
        MockitoAnnotations.initMocks(this);
        optionsController = new OptionsController(userService);
    }

    private void setupAuthenticationWithRoles(String... roles) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority( role));
        }
        doReturn(grantedAuthorities).when(authentication).getAuthorities();
    }

    @Test
    public void testAvailableLengthsForGoogleRole() {
        setupAuthenticationWithRoles("OIDC_USER");
        when(userService.getUserPermissions(List.of("OIDC_USER")))
                .thenReturn(new HashSet<>(Arrays.asList(Permission.URL_ID_24, Permission.URL_ID_32)));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        Set<Integer> actualSet = new HashSet<>(result);
        Set<Integer> expectedSet = new HashSet<>(List.of(32, 24));

        assertEquals(expectedSet, actualSet);
    }

    @Test
    public void testAvailableLengthsForPremiumRole() {
        setupAuthenticationWithRoles("PREMIUM");
        when(userService.getUserPermissions(List.of("PREMIUM")))
                .thenReturn(new HashSet<>(Arrays.asList(Permission.URL_ID_16, Permission.URL_ID_24, Permission.URL_ID_32)));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        Set<Integer> actualSet = new HashSet<>(result);
        Set<Integer> expectedSet = new HashSet<>(List.of(32, 24, 16));

        assertEquals(expectedSet, actualSet);

    }

    @Test
    public void testAvailableLengthsForLifetimeRole() {
        setupAuthenticationWithRoles("LIFETIME");
        when(userService.getUserPermissions(List.of("LIFETIME")))
                .thenReturn(new HashSet<>(Arrays.asList(Permission.URL_ID_8, Permission.URL_ID_16, Permission.URL_ID_24, Permission.URL_ID_32)));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        Set<Integer> actualSet = new HashSet<>(result);
        Set<Integer> expectedSet = new HashSet<>(List.of(32, 24, 16, 8));

        assertEquals(expectedSet, actualSet);
    }

    @Test
    public void testAvailableLengthsForAnonymousRole() {
        setupAuthenticationWithRoles("ANONYMOUS");
        when(userService.getUserPermissions(List.of("ANONYMOUS")))
                .thenReturn(new HashSet<>(Arrays.asList(Permission.URL_ID_32)));

        List<Integer> result = optionsController.getAvailableLengths(authentication);
        assertEquals(List.of(32), result);
    }

    @Test
    public void testAvailableLengthsForNoRole() {
        setupAuthenticationWithRoles();
        when(userService.getUserPermissions(List.of()))
                .thenReturn(new HashSet<>());

        List<Integer> result = optionsController.getAvailableLengths(authentication);
        assertEquals(List.of(32), result);
    }
}
