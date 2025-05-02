package com.hrizzon2.demotest.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecurityUtils implements ISecurityUtils {
    @Value("${jwt.secret}")
    String jwtSecret;

    @Override
    public String getRole(AppUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .findFirst()
                .orElse(null);
    }

    @Override
    public String generateToken(AppUserDetails userDetails) {

        System.out.println(jwtSecret);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .addClaims(Map.of("role", getRole(userDetails))) // on ajoute une revendication - implantation la + classique qu'on peut faire d'un Json
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
//                .setExpiration(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))) // Permet de donner une expiration à la connexion
                .compact();
    }

    @Override
    public String getSubjectFromJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
