package com.danifgx.acortadirecciones.config;

import com.danifgx.acortadirecciones.entity.RolePermission;
import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.dao.UserDao;
import com.danifgx.acortadirecciones.persistence.repository.RolePermissionRepository;
import com.danifgx.acortadirecciones.security.profile.Permission;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RolePermissionRepository rolePermissionRepository;
    private final UserDao userDao;

    public DatabaseInitializer(RolePermissionRepository rolePermissionRepository, UserDao userDao) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userDao = userDao;
    }

    @Override
    public void run(String... args) {
        if (rolePermissionRepository.count() == 0) {
            rolePermissionRepository.save(new RolePermission("OIDC_USER", Set.of(Permission.URL_LENGTH_24, Permission.URL_LENGTH_32)));
            rolePermissionRepository.save(new RolePermission("PREMIUM", Set.of(Permission.URL_LENGTH_16, Permission.URL_LENGTH_24, Permission.URL_LENGTH_32)));
            rolePermissionRepository.save(new RolePermission("LIFETIME", Set.of(Permission.URL_LENGTH_8, Permission.URL_LENGTH_16, Permission.URL_LENGTH_24, Permission.URL_LENGTH_32)));
            rolePermissionRepository.save(new RolePermission("ANONYMOUS", Set.of(Permission.URL_LENGTH_32)));
            rolePermissionRepository.save(new RolePermission("ADMIN", Set.of(Permission.URL_LENGTH_8, Permission.URL_LENGTH_16, Permission.URL_LENGTH_24, Permission.URL_LENGTH_32, Permission.MANAGE_USERS)));
        }

        if (userDao.count() == 0) {
            User newUser = new User();
            newUser.setEmail("danifgx@gmail.com");
            newUser.setRoles(List.of("PREMIUM"));
            userDao.save(newUser);
        }
    }
}
