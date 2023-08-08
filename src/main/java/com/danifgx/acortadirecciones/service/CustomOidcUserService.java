package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserRepository userRepository;

    @Autowired
    CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("Entering LOAD USER");

        OidcUser oidcUser;
        try {
            oidcUser = super.loadUser(userRequest);
        } catch (Exception e) {
            logger.error("Error while loading user from OIDC request", e);
            OAuth2Error oauth2Error = new OAuth2Error("loading_user_error", "Failed to load user from OIDC request", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        try {
            String email = oidcUser.getAttribute("email");
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                // Aquí podrías actualizar la información del usuario si es necesario
                logger.info("User already exists with email: {}", email);
            } else {
                // Si el usuario no está registrado, guárdalo en la base de datos
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setSource("Google-OIDC");
                userRepository.save(newUser);
                logger.info("Registered new user with email: {}", email);
            }

        } catch (Exception e) {
            logger.error("Error processing user data", e);
            OAuth2Error oauth2Error = new OAuth2Error("processing_user_data_error", "Failed to process user data", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        return oidcUser;
    }
}
