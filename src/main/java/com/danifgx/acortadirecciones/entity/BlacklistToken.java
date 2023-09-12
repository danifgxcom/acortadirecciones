package com.danifgx.acortadirecciones.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "blacklistTokens")
@Data
public class BlacklistToken {

    @Id
    private String id;

    @Indexed
    private String token;

    private LocalDateTime addedAt;

    private LocalDateTime expiryDate;

    public BlacklistToken(String token, LocalDateTime addedAt) {
        this.token = token;
        this.addedAt = addedAt;
    }

    public BlacklistToken(String token) {
        this.token = token;
    }
}
