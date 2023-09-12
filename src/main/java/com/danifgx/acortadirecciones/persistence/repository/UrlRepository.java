package com.danifgx.acortadirecciones.persistence.repository;

import com.danifgx.acortadirecciones.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {

    Optional<Url> findByShortenedUrlId(String shortenedUrlId);
    List<Url> findByExpiryDateBefore(LocalDateTime expiryDate);

}
