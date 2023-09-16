package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.User;

import java.util.Optional;

public interface UserService {
    public Optional<User> findByEmail(String email);
    public User registerNewUser(String email);
    public void addRoleToUser(String userId, String roleName);
}
