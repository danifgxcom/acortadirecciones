package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer = csrf -> {
            csrf.disable();
        };

        http
                .csrf(csrfCustomizer)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/home",
                                        "/error",
                                        "/login",
                                        "/login/oauth2/**",
                                        "/oauth2/authorization/**",
                                        "/login/oauth2/code/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2Login(oauth2 -> {
                    oauth2.defaultSuccessUrl("/loggedIn", true)
                            .userInfoEndpoint(userInfoEndpoint -> {
                                userInfoEndpoint.userService(customOAuth2UserService);
                            })
                            .loginPage("/login");
                    ;
                })

        ;
        return http.build();
    }
}
