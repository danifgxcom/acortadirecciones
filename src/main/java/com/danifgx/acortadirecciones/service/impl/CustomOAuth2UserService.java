package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    @Autowired
    CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Entering LOAD USER");

        OAuth2User oAuth2User;
        try {
            oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        } catch (Exception e) {
            logger.error("Error while loading user from OAuth2 request", e);
            OAuth2Error oauth2Error = new OAuth2Error("loading_user_error", "Failed to load user from OAuth2 request", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        try {
            String email = oAuth2User.getAttribute("email");
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                // Aquí podrías actualizar la información del usuario si es necesario
                logger.info("User already exists with email: {}", email);
            } else {
                // Si el usuario no está registrado, guárdalo en la base de datos
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setSource("Google-OAuth2.0");
                userRepository.save(newUser);
                logger.info("Registered new user with email: {}", email);
            }

        } catch (Exception e) {
            logger.error("Error processing user data", e);
            OAuth2Error oauth2Error = new OAuth2Error("processing_user_data_error", "Failed to process user data", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        return oAuth2User;
    }
}
