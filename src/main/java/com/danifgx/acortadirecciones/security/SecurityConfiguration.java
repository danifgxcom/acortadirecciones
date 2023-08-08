package com.danifgx.acortadirecciones.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer = csrf -> {
            csrf.disable();
        };

        http
                .csrf(csrfCustomizer)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/home", "/error", "/login/oauth2/**", "/oauth2/authorization/**", "/login/oauth2/code/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2Login(oauth2 -> {
                    oauth2.defaultSuccessUrl("/loggedIn", true);
                })
        ;
        return http.build();
    }
}
