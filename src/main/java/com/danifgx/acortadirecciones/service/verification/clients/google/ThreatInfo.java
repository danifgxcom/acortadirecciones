package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ThreatInfo {
    private List<String> threatTypes;
    private List<String> platformTypes;
    private List<String> threatEntryTypes;
    private List<UrlThreatEntry> threatEntries;
}