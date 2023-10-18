package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.CustomUserDetails;
import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.repository.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    @Setter
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found");
        }
        log.info("User found: {}", username);
        return new CustomUserDetails(user.get());
    }
}