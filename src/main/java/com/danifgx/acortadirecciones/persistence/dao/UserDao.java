package com.danifgx.acortadirecciones.persistence.dao;

import com.danifgx.acortadirecciones.entity.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(String userId);
    public long count();
}
