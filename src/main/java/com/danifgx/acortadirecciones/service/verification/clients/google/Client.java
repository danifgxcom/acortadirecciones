package com.danifgx.acortadirecciones.service.verification.clients.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String clientId;
    private String clientVersion;
}