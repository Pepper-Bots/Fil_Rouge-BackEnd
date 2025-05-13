package com.hrizzon2.demotest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


// TODO
//  Résumé de ce que fait ce code
//"/auth/**" et "/public/**" sont des endpoints accessibles à tous (login, création de compte...).
//
//"/stagiaire/**" : réservé aux utilisateurs avec le rôle ROLE_STAGIAIRE.
//
//"/admin/**" : réservé aux ROLE_ADMIN.
//
//anyRequest().authenticated() : toute autre route nécessite juste une authentification.
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize sur les méthodes
public class SecurityConfig {

    protected PasswordEncoder passwordEncoder;
    protected UserDetailsService userDetailsService;
    protected JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Value("${app.cors.origins}")
    private String corsOrigins;


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder);
        auth.setUserDetailsService(userDetailsService);
        return auth;
    }

    @Bean
    public SecurityFilterChain configureAuthentification(HttpSecurity http) throws Exception {

        return http
                .csrf(c -> c.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics (ex: login, inscription)
                        .requestMatchers("/auth/**", "/public/**").permitAll()

                        // Endpoints accessibles uniquement aux stagiaires
                        .requestMatchers("/stagiaire/**").hasRole("STAGIAIRE")

                        // Endpoints accessibles uniquement aux admins
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Toute autre requête doit être authentifiée
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(corsOrigins.split(","))); // Utilise la propriété
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);  // Important pour les sessions et l'authentification basée sur les cookies
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
