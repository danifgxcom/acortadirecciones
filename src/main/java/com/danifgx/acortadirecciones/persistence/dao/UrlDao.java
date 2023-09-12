package com.danifgx.acortadirecciones.persistence.dao;

import com.danifgx.acortadirecciones.entity.Url;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlDao {
    Optional<Url> findByShortenedUrlId(String shortenedUrlId);
    List<Url> findByExpiryDateBefore(LocalDateTime expiryDate);
    Url save(Url url);
    void deleteById(String shortenedUrlId);
    void deleteAll(List<Url> urls);
}
