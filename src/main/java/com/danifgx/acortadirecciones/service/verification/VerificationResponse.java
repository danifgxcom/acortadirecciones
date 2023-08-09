package com.danifgx.acortadirecciones.service.verification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationResponse {
    private boolean success;
    private String failedVerifier;

}