package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerNewUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        return userRepository.save(newUser);
    }
}
