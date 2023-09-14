package com.danifgx.acortadirecciones.security;

import com.danifgx.acortadirecciones.filter.JwtAuthenticationFilter;
import com.danifgx.acortadirecciones.service.impl.CustomOidcUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@Slf4j
public class SecurityConfiguration {

    private final CustomOidcUserServiceImpl customOidcUserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(CustomOidcUserServiceImpl customOidcUserService,
                                 JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.customOidcUserService = customOidcUserService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
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
                new AntPathRequestMatcher("/logoutPage"),
                new AntPathRequestMatcher("/oauth2/authorization/**"),
                new AntPathRequestMatcher("/url/**"),
                new AntPathRequestMatcher("/resolve/**"),
                new AntPathRequestMatcher("/favicon.ico"),
                new AntPathRequestMatcher("/option/**")
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
                            .failureUrl("/customErrorPage")
                            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.oidcUserService(customOidcUserService));
                }).formLogin(formLogin -> {
                    formLogin
                            .defaultSuccessUrl("/admin/dashboard", true)
                            .permitAll();
                }).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/logoutPage")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "userData", "jwt_token")
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            log.info("Successfully logged out. Redirecting to /logoutPage");
                            response.sendRedirect("/logoutPage");
                        })
                );

        return http.build();
    }
}
