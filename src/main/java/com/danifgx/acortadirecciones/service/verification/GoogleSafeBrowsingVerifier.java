package com.danifgx.acortadirecciones.service.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GoogleSafeBrowsingVerifier implements UrlVerifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.safebrowsing.api.url}")
    private String apiUrl;

    @Value("${google.safebrowsing.apikey}")
    private String apiKey;

    @Override
    public boolean verify(String url) {
        GoogleSafeBrowsingRequest requestPayload = new GoogleSafeBrowsingRequest("your-client-id", "1.0", new ThreatInfo(List.of(new UrlThreatEntry(url))));

        GoogleSafeBrowsingResponse response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, requestPayload, GoogleSafeBrowsingResponse.class);

        return response == null || response.getMatches() == null || response.getMatches().isEmpty();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GoogleSafeBrowsingRequest {
    private String clientId;
    private String clientVersion;
    private ThreatInfo threatInfo;
}

@Data
@AllArgsConstructor
class ThreatInfo {
    private List<UrlThreatEntry> threatEntries;
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
