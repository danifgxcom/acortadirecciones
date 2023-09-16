package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.dao.RoleDao;
import com.danifgx.acortadirecciones.persistence.dao.UserDao;
import com.danifgx.acortadirecciones.security.profile.Permission;
import com.danifgx.acortadirecciones.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
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
        Role role = roleDao.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found"));

        List<Role> currentRoles = user.getRoles();
        if (currentRoles == null) {
            currentRoles = new ArrayList<>();
        }

        currentRoles.add(role);
        user.setRoles(currentRoles);
        userDao.save(user);
    }


    @Override
    public Set<Permission> getUserPermissions(List<String> roleNames) {
        return null;
    }

    public List<Role> getUserRole(String userId) {
        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }
}
