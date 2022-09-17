package com.example.tattooartistbackend.security;

import com.example.tattooartistbackend.exceptions.UserNotFoundException;
import com.example.tattooartistbackend.security.config.Credentials;
import com.example.tattooartistbackend.security.config.SecurityProperties;
import com.example.tattooartistbackend.security.role.Role;
import com.example.tattooartistbackend.security.role.RoleService;
import com.example.tattooartistbackend.user.User;
import com.example.tattooartistbackend.user.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final SecurityProperties securityProps;
    private final RoleService roleService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        verifyToken(request);
        filterChain.doFilter(request, response);
    }

    public String parseBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (ObjectUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ") && authHeader.length() > 50) ? authHeader.substring(7) : null ;
    }

    private void verifyToken(HttpServletRequest request) {
        FirebaseToken decodedToken = null;
        String token = parseBearerToken(request);
        logger.info("Token: " + token);
        try {
            if (ObjectUtils.isNotEmpty(token)) {
                decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                logger.info("decoded token :  " + decodedToken.getUid());
            }
        } catch (FirebaseAuthException e) {
            logger.error("Firebase Exception:  "+e);
        }
        User user = firebaseTokenToUserDto(decodedToken);
        logger.info("user= " + user);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user != null && decodedToken != null) {
            //if super
            if (securityProps.getSuperAdmins() != null && securityProps.getSuperAdmins().contains(user.getEmail())) {
                if (!decodedToken.getClaims().containsKey(Role.ROLE_SUPER.toString())) {
                    try {
                        roleService.addRole(decodedToken.getUid(), Role.ROLE_SUPER);
                    } catch (Exception e) {
                        logger.error("Super Role registration exception ", e);
                    }
                }
                authorities.add(new SimpleGrantedAuthority(Role.ROLE_SUPER.toString()));
            }
            decodedToken.getClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority(k)));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, new Credentials(decodedToken, token), authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private User firebaseTokenToUserDto(FirebaseToken decodedToken) {
        if (decodedToken != null) {
            return userRepository.findByUid(decodedToken.getUid()).orElse(null);
        }else {
            return null;
        }
    }
}