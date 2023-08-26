package com.lrc.missionO2.security;

import com.lrc.missionO2.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**

The "JWTAuthFilter" class is an implementation of the "OncePerRequestFilter" abstract class.

It is a filter used for processing JSON Web Tokens (JWT) in a web application for authentication purposes.

The "doFilterInternal" method is overridden to intercept incoming requests, extract the JWT token,

validate it using the "JWTTokenProvider", and set the authentication details in the security context

if the token is valid. The "CustomUserDetailsService" is used to load the user details based on the username

extracted from the token. Finally, the request is passed down the filter chain for further processing.

The "getToken" method is a helper method used to extract the JWT token from the request header.

This filter is typically used in the security configuration of a web application to intercept incoming requests


*/
@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {
    private final JWTTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService securityService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = getToken(request);
        String username;
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = securityService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);

    }

    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (bearerToken!=null && bearerToken.startsWith("Bearer")){
            token = bearerToken.substring(7);
        }
        return token;
    }
}
