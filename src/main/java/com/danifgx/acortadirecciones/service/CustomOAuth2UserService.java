package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");

        // Comprueba si el usuario ya está registrado
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (!existingUser.isPresent()) {
            // Si el usuario no está registrado, guárdalo en la base de datos
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setSource("Google-OAuth2.0");
            userRepository.save(newUser);
        }

        // Retorna el usuario autenticado
        return oAuth2User;
    }
}
