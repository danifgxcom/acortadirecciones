package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}