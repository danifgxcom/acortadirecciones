package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.UrlLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlLogRepository extends MongoRepository<UrlLog, String> {

    Optional<UrlLog> findByOriginalUrl(String originalUrl);
}

