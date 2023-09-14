package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.RolePermission;
import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.dao.RoleDao;
import com.danifgx.acortadirecciones.persistence.dao.RolePermissionDao;
import com.danifgx.acortadirecciones.persistence.dao.UserDao;
import com.danifgx.acortadirecciones.security.profile.Permission;
import com.danifgx.acortadirecciones.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final RoleDao roleDao;

    private final RolePermissionDao rolePermissionDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, RolePermissionDao rolePermissionDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.rolePermissionDao = rolePermissionDao;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User registerNewUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        return userDao.save(newUser);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(roleName);
        userDao.save(user);
    }

    @Override
    public Set<Permission> getUserPermissions(List<String> roleNames) {
        log.debug("List of role names: {}", roleNames);
        Set<Permission> permissions = new HashSet<>();

        for (String roleName : roleNames) {
            RolePermission rolePermission = rolePermissionDao.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("RolePermission not found for role: " + roleName));

            permissions.addAll(rolePermission.getPermissions());
        }

        return permissions;
    }
}
