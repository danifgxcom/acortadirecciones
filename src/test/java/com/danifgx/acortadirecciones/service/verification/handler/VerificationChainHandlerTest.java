package com.danifgx.acortadirecciones.service.verification.handler;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.iface.UrlVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class VerificationChainHandlerTest {

    @InjectMocks
    private VerificationChainHandler verificationChainHandler;

    @Mock
    private UrlVerifier mockVerifier1;

    @Mock
    private UrlVerifier mockVerifier2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        List<UrlVerifier> verifiers = Arrays.asList(mockVerifier1, mockVerifier2);
        verificationChainHandler = new VerificationChainHandler(verifiers);
    }

    @Test
    public void testVerifyUrlAllSuccess() {
        when(mockVerifier1.verify("http://example.com")).thenReturn(new VerificationResponse(true, ""));
        when(mockVerifier2.verify("http://example.com")).thenReturn(new VerificationResponse(true, ""));
        
        VerificationResponse result = verificationChainHandler.verifyUrl("http://example.com");
        
        assertTrue(result.isSuccess());
    }

    @Test
    public void testVerifyUrlOneFailure() {
        when(mockVerifier1.verify("http://example.com")).thenReturn(new VerificationResponse(true, ""));
        when(mockVerifier2.verify("http://example.com")).thenReturn(new VerificationResponse(false, "Error message"));
        
        VerificationResponse result = verificationChainHandler.verifyUrl("http://example.com");
        
        assertFalse(result.isSuccess());
    }

    @Test
    public void testVerifyUrlAllFailure() {
        when(mockVerifier1.verify("http://example.com")).thenReturn(new VerificationResponse(false, "Error 1"));
        when(mockVerifier2.verify("http://example.com")).thenReturn(new VerificationResponse(false, "Error 2"));
        
        VerificationResponse result = verificationChainHandler.verifyUrl("http://example.com");
        
        assertFalse(result.isSuccess());
    }
}
