package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.security.profile.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    public Optional<User> findByEmail(String email);
    public User registerNewUser(String email);
    public void addRoleToUser(String userId, String roleName);
    public Set<Permission> getUserPermissions(List<String> roleNames);
}
