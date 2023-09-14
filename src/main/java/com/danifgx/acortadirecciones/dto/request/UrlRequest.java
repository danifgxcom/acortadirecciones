package com.danifgx.acortadirecciones.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @NotNull(message = "URL to shorten cannot be null")
    private String url;

    @Value("${url.lengthId.default}")
    private int length;
    @Value("${url.expirationHours.default}")
    private int expirationHours;

    @AssertTrue(message = "Length must be 8, 16, 24, or 32")
    public boolean isValidLength() {
        return length == 8 || length == 16 || length == 24 || length == 32;
    }

    @AssertTrue(message = "Expiration hours must be 24, 48, or 72")
    public boolean isValidExpirationHours() {
        return expirationHours == 24 || expirationHours == 48 || expirationHours == 72;
    }
}
