package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
    List<Url> findByExpiryDateBefore(LocalDateTime expiryDate);
}
