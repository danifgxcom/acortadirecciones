package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.filter.JwtAuthenticationFilter;
import com.danifgx.acortadirecciones.service.impl.CustomOidcUserService;
import com.danifgx.acortadirecciones.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    @Autowired
    public SecurityConfiguration(CustomOidcUserService customOidcUserService,
                                 JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsServiceImpl customUserDetailsServiceImpl) {
        this.customOidcUserService = customOidcUserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsServiceImpl = customUserDetailsServiceImpl;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, CustomUserDetailsServiceImpl customUserDetailsServiceImpl)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        RequestMatcher publicUrls = new OrRequestMatcher(
                new AntPathRequestMatcher("/admin/login"),
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
                        authorizeRequests.requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers(publicUrls).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> {
                    oauth2
                            .defaultSuccessUrl("/loggedIn")
                            .userInfoEndpoint(userInfoEndpoint ->
                                    userInfoEndpoint.oidcUserService(customOidcUserService))
                            .loginPage("/login");
                }).formLogin(formLogin -> {
                    formLogin
                            .defaultSuccessUrl("/admin/dashboard", true)
                            .permitAll();
                });

        return http.build();
    }
}
