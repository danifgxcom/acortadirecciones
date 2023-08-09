package com.danifgx.acortadirecciones.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "urlLogs")
public class UrlLog {

    @Id
    private String id;

    private String originalUrl;

    private String shortenedBaseUrl;

    private String shortenedUrlId;

    private LocalDateTime creationDate;
}
