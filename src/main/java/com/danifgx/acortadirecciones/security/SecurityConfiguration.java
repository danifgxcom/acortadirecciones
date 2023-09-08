package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.filter.JwtAuthenticationFilter;
import com.danifgx.acortadirecciones.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomOidcUserService customOidcUserService;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfiguration(CustomOidcUserService customOidcUserService,
                                 JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOidcUserService = customOidcUserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        RequestMatcher publicUrls = new OrRequestMatcher(
                new AntPathRequestMatcher("/home"),
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/url/**"),
                new AntPathRequestMatcher("/resolve/**"),
                new AntPathRequestMatcher("/favicon.ico")
        );

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(publicUrls).permitAll()
                                .anyRequest().authenticated().and()
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .oauth2Login(oauth2 -> {
                    oauth2
                            .defaultSuccessUrl("/loggedIn")
                            .userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.oidcUserService(customOidcUserService))
                            .loginPage("/login");
                });

        return http.build();
    }
}
