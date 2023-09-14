package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOidcUserServiceImpl extends OidcUserService {

    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            String email = oidcUser.getAttribute("email");
            OidcIdToken idToken = userRequest.getIdToken();
            if (idToken == null) {
                log.error("idToken is null");
                throw new OAuth2AuthenticationException(new OAuth2Error("null_id_token", "ID Token is null", null));
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> userServiceImpl.registerNewUser(email));

            oidcUser = convertUserToOidcUser(user, idToken);

        } catch (Exception e) {
            log.error(e.getMessage());
            OAuth2Error oauth2Error = new OAuth2Error("processing_user_data_error", "Failed to process user data", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        return oidcUser;
    }

    public OidcUser convertUserToOidcUser(User user) {
        return convertUserToOidcUser(user, null);
    }

    public OidcUser convertUserToOidcUser(User user, OidcIdToken idToken) {
        Map<String, Object> attributes = Collections.singletonMap("email", user.getEmail());
        OidcUserInfo oidcUserInfo = new OidcUserInfo(attributes);

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        if (idToken != null) {
            OidcUserAuthority oidcUserAuthority = new OidcUserAuthority(idToken, oidcUserInfo);
            authorities.add(oidcUserAuthority);
            return new DefaultOidcUser(authorities, idToken, oidcUserInfo, "email");
        } else {
            return new DefaultOidcUser(authorities, null, "email");
        }
    }

}
