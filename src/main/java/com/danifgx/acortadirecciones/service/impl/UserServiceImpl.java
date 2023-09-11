package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.repository.UserRepository;
import com.danifgx.acortadirecciones.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerNewUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        return userRepository.save(newUser);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(roleName);
        userRepository.save(user);
    }
}
