package com.danifgx.acortadirecciones.persistence.repository;

import com.danifgx.acortadirecciones.entity.BlacklistToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlacklistTokenRepository extends MongoRepository<BlacklistToken, String> {
    Optional<BlacklistToken> findByToken(String token);
    List<BlacklistToken> findAllByExpiryDateBefore(LocalDateTime expiryDate);
}
