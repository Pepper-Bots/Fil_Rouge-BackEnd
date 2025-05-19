package com.hrizzon2.demotest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.TimeZone;

@SpringBootApplication
public class DemotestApplication {

    @Value("${email.address")
    String emailAddress;

    @Value("${email.password}")
    String emailPassword;

    public static void main(String[] args) {

        SpringApplication.run(DemotestApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    // On met la dépendance ici plutot que dans l'application sinon ça faite une dépendance récursive
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // a chaque fois que j'aurais besoin d'un objet password encoder -> je recupère un BCryptPasswordEncoder
    }
}

