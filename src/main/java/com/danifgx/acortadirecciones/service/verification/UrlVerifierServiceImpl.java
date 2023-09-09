package com.danifgx.acortadirecciones.service.verification;

import com.danifgx.acortadirecciones.exception.VerificationFailedException;
import com.danifgx.acortadirecciones.service.UrlRecordServiceImpl;
import com.danifgx.acortadirecciones.service.verification.handler.VerificationChainHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlVerifierServiceImpl {

    @Value("${base.url}")
    private String baseUrl;
    private final VerificationChainHandler verificationHandler;
    private final UrlRecordServiceImpl urlRecordServiceImpl;

    public UrlVerifierServiceImpl(VerificationChainHandler verificationHandler, UrlRecordServiceImpl urlRecordServiceImpl) {
        this.verificationHandler = verificationHandler;
        this.urlRecordServiceImpl = urlRecordServiceImpl;
    }

    public void verifyUrl(String originalUrl) {
        VerificationResponse response = verificationHandler.verifyUrl(originalUrl);
        if (!response.isSuccess()) {
            throw new VerificationFailedException("URL verification failed! Rejected by: " + response.getFailedVerifier());
        }
        if (isUrlAlreadyShortened(originalUrl)) {
            throw new VerificationFailedException("The URL is already shortened in our system!");
        }
    }


    private boolean isUrlAlreadyShortened(String url) {
        String pattern = baseUrl + ".*";
        return url.matches(pattern);
    }
}
