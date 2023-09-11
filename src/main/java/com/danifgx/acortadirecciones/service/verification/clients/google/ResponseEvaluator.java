package com.danifgx.acortadirecciones.service.verification.clients.google;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResponseEvaluator {
    public VerificationResponse evaluate(GoogleSafeBrowsingResponse responseBody, String url) {
        if (responseBody != null && responseBody.getMatches() != null && !responseBody.getMatches().isEmpty()) {
            String threatType = responseBody.getMatches().get(0).getThreatType();
            log.warn("URL verification failed: {}. Threat type: {}", url, threatType);
            return new VerificationResponse(false, this.getClass().getSimpleName() + " - " + threatType);
        }

        log.info("URL passed Google Safe Browsing verification: {}", url);
        return new VerificationResponse(true, "");
    }
}
