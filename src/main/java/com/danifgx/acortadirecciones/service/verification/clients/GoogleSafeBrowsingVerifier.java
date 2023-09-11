package com.danifgx.acortadirecciones.service.verification.clients;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.clients.google.*;
import com.danifgx.acortadirecciones.service.verification.iface.UrlVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class GoogleSafeBrowsingVerifier implements UrlVerifier {

    private static final List<String> allThreatTypes = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION");
    private final RestTemplate restTemplate;
    private final ResponseEvaluator responseEvaluator;

    private final ApiCaller apiCaller;

    @Value("${google.safebrowsing.api-key}")
    private String apiKey;
    @Value("${google.safebrowsing.client-id}")
    private String clientId;
    @Value("${google.safebrowsing.client-version}")
    private String clientVersion;
    @Value("${google.safebrowsing.endpoint}")
    private String endpoint;

    @Autowired
    public GoogleSafeBrowsingVerifier(RestTemplate restTemplate, ResponseEvaluator responseEvaluator, ApiCaller apiCaller) {
        this.apiCaller = apiCaller;
        this.restTemplate = restTemplate;
        this.responseEvaluator = responseEvaluator;
    }


    public VerificationResponse verify(String url) {
        log.info("Verifying URL with Google Safe Browsing: {}", url);
        HttpEntity<GoogleSafeBrowsingRequest> entity = createHttpEntity(url);

        try {
            GoogleSafeBrowsingResponse responseBody = apiCaller.makeApiCall(restTemplate, entity, endpoint, apiKey);
            return responseEvaluator.evaluate(responseBody, url);
        } catch (Exception e) {
            log.error("Error occurred while verifying URL with Google Safe Browsing: {}. Error: {}", url, e.getMessage());
            return new VerificationResponse(false, this.getClass().getSimpleName());
        }
    }


    private HttpEntity<GoogleSafeBrowsingRequest> createHttpEntity(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        GoogleSafeBrowsingRequest requestPayload = new GoogleSafeBrowsingRequest(
                new Client(clientId, clientVersion),
                new ThreatInfo(
                        allThreatTypes,
                        Arrays.asList("WINDOWS", "LINUX", "OSX", "IOS", "ANDROID", "CHROME"),
                        Arrays.asList("URL"),
                        Arrays.asList(new UrlThreatEntry(url))
                )
        );

        return new HttpEntity<>(requestPayload, headers);
    }

    private GoogleSafeBrowsingResponse makeApiCall(HttpEntity<GoogleSafeBrowsingRequest> entity) {
        ResponseEntity<GoogleSafeBrowsingResponse> response = restTemplate.exchange(
                endpoint + apiKey,
                HttpMethod.POST,
                entity,
                GoogleSafeBrowsingResponse.class
        );
        return response.getBody();
    }

    public GoogleSafeBrowsingResponse buildGoogleSafeBrowsingResponse() {
        GoogleSafeBrowsingResponse response = new GoogleSafeBrowsingResponse();
        ThreatMatch match = new ThreatMatch();
        match.setThreatType("MALWARE");
        response.setMatches(List.of(match));
        return response;
    }
}
