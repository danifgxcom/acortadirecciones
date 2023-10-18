package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.entity.User;
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

    private User ayuser;

    private OptionsController optionsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        optionsController = new OptionsController(userService);
    }

    void setupAuthenticationWithRoles(String... roles) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        doReturn(grantedAuthorities).when(authentication).getAuthorities();

        ayuser = new User();
        ayuser.setEmail("ayuser@comunidad.mandril.es");
        when(authentication.getPrincipal()).thenReturn(ayuser);
    }

    @Test
    void testAvailableLengthsForGoogleRole() {
        setupAuthenticationWithRoles("OIDC_USER");

        when(userService.getUserUrlLengths(ayuser.getId())).thenReturn(List.of(24, 32));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(24, 32), result);
    }

    @Test
    void testAvailableLengthsForPremiumRole() {
        setupAuthenticationWithRoles("PREMIUM");

        when(userService.getUserUrlLengths(ayuser.getId())).thenReturn(List.of(16, 24, 32));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(16, 24, 32), result);
    }

    @Test
    void testAvailableLengthsForLifetimeRole() {
        setupAuthenticationWithRoles("LIFETIME");

        when(userService.getUserUrlLengths(ayuser.getId())).thenReturn(List.of(8, 16, 24, 32));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(8, 16, 24, 32), result);
    }

    @Test
    void testAvailableLengthsForAnonymousRole() {
        setupAuthenticationWithRoles("ANONYMOUS");

        when(userService.getUserUrlLengths(ayuser.getId())).thenReturn(List.of(32));

        List<Integer> result = optionsController.getAvailableLengths(authentication);

        assertEquals(List.of(32), result);
    }

    @Test
    void shoudReturn24_whenCallingAsAnonymous() {
        setupAuthenticationWithRoles("ANONYMOUS");

        when(userService.getUserUrlLengths(ayuser.getId())).thenReturn(List.of(24, 32));

        List<Integer> result = optionsController.getAvailableExpirationTimes(authentication);

        assertEquals(List.of(24), result);

    }

    @Test
    void shoudReturn24And48_whenCallingAsGoogle() {
        setupAuthenticationWithRoles("ANONYMOUS");

        when(authentication.getDetails()).thenReturn("24, 48");

        List<Integer> result = optionsController.getAvailableExpirationTimes(authentication);

        assertEquals(List.of(24, 48), result);

    }
}
