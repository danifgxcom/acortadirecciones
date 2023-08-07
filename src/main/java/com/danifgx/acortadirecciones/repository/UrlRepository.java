package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends MongoRepository<Url, String> {
    List<Url> findByExpiryDateBefore(LocalDateTime expiryDate);
}
