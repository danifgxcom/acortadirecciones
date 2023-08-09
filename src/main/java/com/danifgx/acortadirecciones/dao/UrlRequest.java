package com.danifgx.acortadirecciones.dao;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    private String url;
    private int expirationHours;
}
