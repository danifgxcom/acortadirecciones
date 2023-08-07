package com.danifgx.acortadirecciones.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "urls")
public class Url {

    @Id
    private String id;

    @NonNull
    private String originalUrl;

    private LocalDateTime creationDate;

    @Indexed(expireAfterSeconds = 24 * 60 * 60)
    private LocalDateTime expiryDate;
}
