package com.danifgx.acortadirecciones.service.verification.iface;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;

public interface UrlVerifier {
    VerificationResponse verify(String url);
}
