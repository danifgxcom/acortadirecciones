package com.danifgx.acortadirecciones.service.iface;

import com.danifgx.acortadirecciones.entity.User;

import java.util.Optional;

public interface UserService {
    public Optional<User> findByEmail(String email);
    public User registerNewUser(String email);
}
