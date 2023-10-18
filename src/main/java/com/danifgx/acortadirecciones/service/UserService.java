package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    User registerNewUser(String email);
    void addRoleToUser(String userId, String roleName);
    void updateUserRoleAndAttributes(String userId, String newRoleName);
    List<Integer> getUserUrlLengths(String userId);
    List<Integer> getUserExpirationTimes(String userId);
    List<Role> getUserRoles(String userId);
}
