package com.hrizzon2.demotest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    protected ISecurityUtils securityUtils;
    protected UserDetailsService userDetailsService;

    @Autowired
    public JwtFilter(ISecurityUtils securityUtils, UserDetailsService userDetailsService) {
        this.securityUtils = securityUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Ignorer les endpoints d'authentification
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Récupérer le token Authorization
        String token = request.getHeader("Authorization");

        // 3. Vérifier le format Bearer
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7);

                // 4. Vérifier que le JWT n'est pas vide
                if (!jwt.isEmpty()) {
                    String subject = securityUtils.getSubjectFromJwt(jwt);

                    if (subject != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

                        // Voir doc pour partie suivante :
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (Exception e) {
                // Log l'erreur mais ne pas faire planter l'application
                System.out.println("Erreur JWT : " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
