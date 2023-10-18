package com.danifgx.acortadirecciones.config;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.dao.UserDao;
import com.danifgx.acortadirecciones.persistence.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserDao userDao;

    public DatabaseInitializer(RoleRepository roleRepository, UserDao userDao) {
        this.roleRepository = roleRepository;
        this.userDao = userDao;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("OIDC_USER", Arrays.asList(24, 32), Arrays.asList(24)));
            roleRepository.save(new Role("PREMIUM", Arrays.asList(16, 24, 32), Arrays.asList(24, 48)));
            roleRepository.save(new Role("LIFETIME", Arrays.asList(8, 16, 24, 32), Arrays.asList(24, 48, 72)));
            roleRepository.save(new Role("ANONYMOUS", Arrays.asList(32), Arrays.asList(24)));
            roleRepository.save(new Role("ADMIN", Arrays.asList(8, 16, 24, 32), Arrays.asList(24, 48, 72)));
        }

        if (userDao.count() == 0) {
            User newUser = new User();
            newUser.setEmail("danifgx@gmail.com");
            Optional<Role> premiumRole = roleRepository.findByName("LIFETIME");
            if (premiumRole.isPresent()) {
                newUser.setRoles(List.of(premiumRole.get()));
            }
            userDao.save(newUser);
        }

    }
}
