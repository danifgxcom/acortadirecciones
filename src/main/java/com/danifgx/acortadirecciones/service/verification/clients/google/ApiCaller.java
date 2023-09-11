package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
public class ApiCaller {
    public GoogleSafeBrowsingResponse makeApiCall(RestTemplate restTemplate, HttpEntity<GoogleSafeBrowsingRequest> entity, String endpoint, String apiKey) {
        try {
            ResponseEntity<GoogleSafeBrowsingResponse> response = restTemplate.exchange(
                    endpoint + apiKey,
                    HttpMethod.POST,
                    entity,
                    GoogleSafeBrowsingResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error during API call: {}", e.getMessage());
                return createErrorResponse();
        }
    }
    private GoogleSafeBrowsingResponse createErrorResponse() {
        GoogleSafeBrowsingResponse errorResponse = new GoogleSafeBrowsingResponse();
        ThreatMatch errorMatch = new ThreatMatch();
        errorMatch.setThreatType("API_ERROR");
        errorResponse.setMatches(List.of(errorMatch));
        return errorResponse;
    }

}
