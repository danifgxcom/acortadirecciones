package com.danifgx.acortadirecciones.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "urlLogs")
@Builder
public class UrlLog {

    @Id
    private String id;

    private String originalUrl;

    private String shortenedBaseUrl;

    private String shortenedUrlId;

    private LocalDateTime creationDate;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private String createdBy;
}
