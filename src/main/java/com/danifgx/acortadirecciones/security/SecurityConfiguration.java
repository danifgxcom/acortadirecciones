package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomOidcUserService customOidcUserService;

    public SecurityConfiguration(CustomOidcUserService customOidcUserService) {
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        RequestMatcher publicUrls = new OrRequestMatcher(
                new AntPathRequestMatcher("/home"),
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/login/oauth2/**"),
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/login/oauth2/code/**"),
                new AntPathRequestMatcher("/url", "GET"),
                new AntPathRequestMatcher("/url/**", "POST")
        );

        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(publicUrls).permitAll()
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
