package com.danifgx.acortadirecciones.persistence.dao;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.entity.UrlLog;

import java.util.Optional;

public interface UrlLogDao {
    void save(UrlLog urlLog);
    Optional<UrlLog> findByShortenedUrlId(String shortUrlId);
    Optional<UrlLog> findByOriginalUrl(String originalUrl);

    interface RoleDao {
        Optional<Role> findByName(String name);
        Role save(Role role);
    }
}
