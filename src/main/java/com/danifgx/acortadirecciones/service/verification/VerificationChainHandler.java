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

    public boolean verifyUrl(String url) {
        for (UrlVerifier verifier : verifiers) {
            if (!verifier.verify(url)) {
                return false;
            }
        }
        return true;
    }
}
