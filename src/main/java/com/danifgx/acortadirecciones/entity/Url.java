package com.danifgx.acortadirecciones.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "urls")
@EqualsAndHashCode (exclude = {"creationDAte", "expiryDate"})
public class Url {

    @Id
    private String id;

    @NonNull
    private String originalUrl;

    private String shortenedUrlId;

    private String shortenedBaseUrl;

    private LocalDateTime creationDate;

    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiryDate;
}
