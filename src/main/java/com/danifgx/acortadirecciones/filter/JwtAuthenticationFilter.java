package com.danifgx.acortadirecciones.filter;

import com.danifgx.acortadirecciones.entity.User;
import com.danifgx.acortadirecciones.security.profile.Permission;
import com.danifgx.acortadirecciones.service.JwtService;
import com.danifgx.acortadirecciones.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Starting authentication filter");

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            String email = jwtService.extractUsername(jwt);
            if (jwtService.validateToken(jwt, email)) {
                log.info("JWT Token is valid for email: {}", email);

                Optional<User> optionalUser = userService.findByEmail(email);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    List<String> roles = user.getRoles();
                    Set<Permission> userPermissions = userService.getUserPermissions(roles);
                    List<GrantedAuthority> grantedAuthorities = userPermissions.stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.name()))
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            user, null, grantedAuthorities);

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    log.warn("No user found with email: {}", email);
                }
            } else {
                log.warn("JWT Token is invalid for email: {}", email);
            }
        } else {
            log.warn("Authorization header is invalid or missing");
        }

        filterChain.doFilter(request, response);
    }
}
