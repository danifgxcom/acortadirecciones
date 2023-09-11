package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.Data;

import java.util.List;

@Data
public class GoogleSafeBrowsingResponse {
    private List<ThreatMatch> matches;
}