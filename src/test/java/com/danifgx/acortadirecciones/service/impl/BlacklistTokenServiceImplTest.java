package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.BlacklistToken;
import com.danifgx.acortadirecciones.persistence.repository.BlacklistTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BlacklistTokenServiceImplTest {

    @Mock
    private BlacklistTokenRepository blacklistTokenRepository;

    @InjectMocks
    private BlacklistTokenServiceImpl blacklistTokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddToBlacklist() {
        String token = "testToken";
        blacklistTokenService.addToBlacklist(token);

        verify(blacklistTokenRepository, times(1)).save(new BlacklistToken(token));
    }

    @Test
    public void testIsBlacklisted() {
        String token = "testToken";
        when(blacklistTokenRepository.existsById(token)).thenReturn(true);

        boolean result = blacklistTokenService.isBlacklisted(token);

        assertTrue(result);
        verify(blacklistTokenRepository, times(1)).existsById(token);
    }

    @Test
    public void testRemoveFromBlacklist() {
        String token = "testToken";

        blacklistTokenService.removeFromBlacklist(token);

        verify(blacklistTokenRepository, times(1)).deleteById(token);
    }

    @Test
    public void testPurgeExpiredTokens() {
        BlacklistToken expiredToken1 = new BlacklistToken("expiredToken1");
        BlacklistToken expiredToken2 = new BlacklistToken("expiredToken2");
        when(blacklistTokenRepository.findAllByExpiryDateBefore(any(LocalDateTime.class))).thenReturn(Arrays.asList(expiredToken1, expiredToken2));

        blacklistTokenService.purgeExpiredTokens();

        verify(blacklistTokenRepository, times(1)).findAllByExpiryDateBefore(any(LocalDateTime.class));
        verify(blacklistTokenRepository, times(1)).deleteAll(Arrays.asList(expiredToken1, expiredToken2));
    }

}
