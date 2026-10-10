package com.vishnu.banking_application.security;

import com.vishnu.banking_application.security.repository.RevokedTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {

            String username = jwtService.extractUsername(jwt);
            String tokenId = jwtService.extractTokenId(jwt);

            if (revokedTokenRepository.existsByTokenId(tokenId)) {

                authenticationEntryPoint.commenceInvalidToken(
                        request,
                        response
                );

                return;
            }

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            boolean valid =
                    jwtService.validateToken(jwt, userDetails);

            if (!valid) {

                authenticationEntryPoint.commenceInvalidToken(
                        request,
                        response
                );

                return;
            }

            long tokenVersion =
                    jwtService.extractTokenVersion(jwt);

            CustomUserDetails customUserDetails =
                    (CustomUserDetails) userDetails;

            if (tokenVersion !=
                    customUserDetails.getUser().getTokenVersion()) {

                authenticationEntryPoint.commenceInvalidToken(
                        request,
                        response
                );

                return;
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (JwtException
                 | UsernameNotFoundException
                 | IllegalArgumentException exception) {

            authenticationEntryPoint.commenceInvalidToken(
                    request,
                    response
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}