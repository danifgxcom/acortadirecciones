package com.danifgx.acortadirecciones.service.verification;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class VerificationChainHandler {

    private List<UrlVerifier> verifiers = new ArrayList<>();

    public void addVerifier(UrlVerifier verifier) {
        verifiers.add(verifier);
    }

    public VerificationResponse verifyUrl(String url) {
        for (UrlVerifier verifier : verifiers) {
            VerificationResponse response = verifier.verify(url);
            if (!response.isSuccess()) {
                return response;
            }
        }
        return new VerificationResponse(true, "");
    }
}
