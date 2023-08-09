package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.service.verification.VerificationChainHandler;
import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlVerifierService {

    @Value("${base.url}")
    private String baseUrl;
    private final VerificationChainHandler verificationHandler;
    private final UrlRecordService urlRecordService;

    public UrlVerifierService(VerificationChainHandler verificationHandler, UrlRecordService urlRecordService) {
        this.verificationHandler = verificationHandler;
        this.urlRecordService = urlRecordService;
    }

    public void verifyUrl(String originalUrl) {
        VerificationResponse response = verificationHandler.verifyUrl(originalUrl);
        if (!response.isSuccess()) {
            throw new IllegalArgumentException("¡URL no es segura o no está permitida! Rechazado por: " + response.getFailedVerifier());
        }
        if (isUrlAlreadyShortened(originalUrl)) {
            throw new IllegalArgumentException("La URL ya está acortada en nuestro sistema!");
        }
    }

    private boolean isUrlAlreadyShortened(String url) {
        String pattern = baseUrl + ".*";
        return url.matches(pattern);
    }
}
