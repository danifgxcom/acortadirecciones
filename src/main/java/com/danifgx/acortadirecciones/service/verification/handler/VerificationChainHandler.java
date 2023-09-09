package com.danifgx.acortadirecciones.service.verification.handler;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.iface.UrlVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationChainHandler {

    private List<UrlVerifier> verifiers;

    @Autowired
    public VerificationChainHandler(List<UrlVerifier> verifiers) {
        this.verifiers = verifiers;
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
