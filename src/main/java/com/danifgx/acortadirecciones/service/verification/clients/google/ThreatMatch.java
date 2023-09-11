package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreatMatch {
    private String threatType;
    private String platformType;
    private String threatEntryType;
    private UrlThreatEntry threat;
}