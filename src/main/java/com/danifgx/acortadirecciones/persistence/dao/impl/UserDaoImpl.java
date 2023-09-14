package com.danifgx.acortadirecciones.persistence.dao.impl;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.dao.UserDao;
import com.danifgx.acortadirecciones.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Autowired
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
