package com.danifgx.acortadirecciones.service.validation.verification.clients;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.clients.GoogleSafeBrowsingVerifier;
import com.danifgx.acortadirecciones.service.verification.clients.google.ApiCaller;
import com.danifgx.acortadirecciones.service.verification.clients.google.GoogleSafeBrowsingResponse;
import com.danifgx.acortadirecciones.service.verification.clients.google.ResponseEvaluator;
import com.danifgx.acortadirecciones.service.verification.clients.google.ThreatMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GoogleSafeBrowsingVerifierTest {


    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEvaluator responseEvaluator;

    @Mock
    private ApiCaller apiCaller;

    @InjectMocks
    private GoogleSafeBrowsingVerifier googleSafeBrowsingVerifier;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVerifyUrlIsSafe() {
        // Mocks and setup
        GoogleSafeBrowsingResponse mockResponse = buildGoogleSafeBrowsingResponse();
        mockResponse.setMatches(null);

        when(apiCaller.makeApiCall(any(), any(), any(), any())).thenReturn(mockResponse);
        when(responseEvaluator.evaluate(any(), any())).thenReturn(new VerificationResponse(true, ""));


        VerificationResponse result = googleSafeBrowsingVerifier.verify("http://example.com");

        // Assertions
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("", result.getName());
    }


    @Test
    public void testVerifyUrlIsUnsafe() {
        GoogleSafeBrowsingResponse mockResponse = buildGoogleSafeBrowsingResponse();
        ThreatMatch match = new ThreatMatch();
        match.setThreatType("MALWARE");
        mockResponse.setMatches(List.of(match));

        when(apiCaller.makeApiCall(any(), any(), any(), any())).thenReturn(mockResponse);
        when(responseEvaluator.evaluate(any(), any())).thenReturn(new VerificationResponse(false, "GoogleSafeBrowsingVerifier - MALWARE"));

        VerificationResponse result = googleSafeBrowsingVerifier.verify("http://example.com");

        assertFalse(result.isSuccess());
        assertEquals("GoogleSafeBrowsingVerifier - MALWARE", result.getName());
    }

    @Test
    public void testVerifyUrlApiCallFails() {
        when(apiCaller.makeApiCall(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("API call failed"));

        VerificationResponse result = googleSafeBrowsingVerifier.verify("http://example.com");

        assertFalse(result.isSuccess());
        assertEquals("GoogleSafeBrowsingVerifier", result.getName());
    }





    private GoogleSafeBrowsingResponse buildGoogleSafeBrowsingResponse() {
        GoogleSafeBrowsingResponse response = new GoogleSafeBrowsingResponse();
        ThreatMatch match = new ThreatMatch();
        match.setThreatType("MALWARE");
        response.setMatches(List.of(match));
        return response;
    }

}
