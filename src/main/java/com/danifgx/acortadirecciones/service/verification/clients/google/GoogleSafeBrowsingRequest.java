package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleSafeBrowsingRequest {
    private Client client;
    private ThreatInfo threatInfo;
}