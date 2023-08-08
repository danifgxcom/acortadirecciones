package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private CustomOidcUserService customOidcUserService;

    @Autowired
    public SecurityConfiguration(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(
                                        "/home",
                                        "/error",
                                        "/login",
                                        "/login/oauth2/**",
                                        "/oauth2/authorization/**",
                                        "/login/oauth2/code/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> {
                    oauth2
                            .defaultSuccessUrl("/loggedIn", true)
                            .userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.oidcUserService(customOidcUserService))
                            .loginPage("/login");
                });

        return http.build();
    }
}
