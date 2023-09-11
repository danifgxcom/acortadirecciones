package com.danifgx.acortadirecciones.service.validation.verification.clients;

import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.clients.UrlBlacklistVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UrlBlacklistVerifierTest {

    @Mock
    private UtilsService utilsService;

    private String blacklistFilePath="blacklists/domains2.list";

    private UrlBlacklistVerifier urlBlacklistVerifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Set<String> blacklistedUrls = new HashSet<>();
        blacklistedUrls.add("zzzoolight.co.za0-i-fdik.000webhostapp.com");

        urlBlacklistVerifier = new UrlBlacklistVerifier(blacklistFilePath, utilsService);
    }

    @Test
    void verify_BlacklistedUrl_Failure() throws MalformedURLException {
        String url = "https://zzzoolight.co.za0-i-fdik.000webhostapp.com";
        when(utilsService.extractDomain(url)).thenReturn("zzzoolight.co.za0-i-fdik.000webhostapp.com");

        VerificationResponse response = urlBlacklistVerifier.verify(url);

        assertFalse(response.isSuccess());
    }

    @Test
    void verify_NotBlacklistedUrl_Success() throws MalformedURLException {
        String url = "https://www.google.com";
        when(utilsService.extractDomain(url)).thenReturn("www.google.com");

        VerificationResponse response = urlBlacklistVerifier.verify(url);

        assertTrue(response.isSuccess());
    }

    @Test
    void verify_MalformedUrl_Failure() throws MalformedURLException {
        String url = "malformed-url";
        when(utilsService.extractDomain(url)).thenThrow(new MalformedURLException());

        VerificationResponse response = urlBlacklistVerifier.verify(url);

        assertFalse(response.isSuccess());
    }
}
