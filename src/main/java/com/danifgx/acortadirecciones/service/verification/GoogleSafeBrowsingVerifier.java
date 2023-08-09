package com.danifgx.acortadirecciones.service.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GoogleSafeBrowsingVerifier implements UrlVerifier {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSafeBrowsingVerifier.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.safebrowsing.api-key}")
    private String apiKey;

    @Value("${google.safebrowsing.client-id}")
    private String clientId;

    @Value("${google.safebrowsing.client-version}")
    private String clientVersion;

    @Value("${google.safebrowsing.endpoint}")
    private String endpoint;

    public VerificationResponse verify(String url) {
        logger.info("Verifying URL with Google Safe Browsing: {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> allThreatTypes = Arrays.asList("THREAT_TYPE_UNSPECIFIED", "MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION");


        GoogleSafeBrowsingRequest requestPayload = new GoogleSafeBrowsingRequest(
                new GoogleSafeBrowsingRequest.Client(clientId, clientVersion),
                new GoogleSafeBrowsingRequest.ThreatInfo(
                        allThreatTypes,
                        Arrays.asList("WINDOWS", "LINUX", "OSX", "IOS", "ANDROID", "CHROME"),
                        Arrays.asList("URL"),
                        Arrays.asList(new UrlThreatEntry(url))
                )

        );

        HttpEntity<GoogleSafeBrowsingRequest> entity = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<GoogleSafeBrowsingResponse> response = restTemplate.exchange(
                    endpoint + apiKey,
                    HttpMethod.POST,
                    entity,
                    GoogleSafeBrowsingResponse.class
            );

            GoogleSafeBrowsingResponse responseBody = response.getBody();
            if (responseBody != null && responseBody.getMatches() != null && !responseBody.getMatches().isEmpty()) {
                String threatType = responseBody.getMatches().get(0).getThreatType();
                logger.warn("URL verification failed: {}. Threat type: {}", url, threatType);
                return new VerificationResponse(false, this.getClass().getSimpleName() + " - " + threatType);
            }

            logger.info("URL passed Google Safe Browsing verification: {}", url);
            return new VerificationResponse(true, "");

        } catch (Exception e) {
            logger.error("Error occurred while verifying URL with Google Safe Browsing: {}. Error: {}", url, e.getMessage());
            return new VerificationResponse(false, this.getClass().getSimpleName());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class GoogleSafeBrowsingRequest {
        private Client client;
        private ThreatInfo threatInfo;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        static class Client {
            private String clientId;
            private String clientVersion;
        }

        @Data
        @AllArgsConstructor
        static class ThreatInfo {
            private List<String> threatTypes;
            private List<String> platformTypes;
            private List<String> threatEntryTypes;
            private List<UrlThreatEntry> threatEntries;
        }
    }
}

@Data
@AllArgsConstructor
class UrlThreatEntry {
    private String url;
}

@Data
class GoogleSafeBrowsingResponse {
    private List<ThreatMatch> matches;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ThreatMatch {
    private String threatType;
    private String platformType;
    private String threatEntryType;
    private UrlThreatEntry threat;
}
