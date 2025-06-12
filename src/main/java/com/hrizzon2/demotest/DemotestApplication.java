package com.hrizzon2.demotest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;
import java.util.TimeZone;

// Annotation : pour signifier que le programme se base sur ce qui est écrit ici, pas ailleurs
@SpringBootApplication
public class DemotestApplication {

    @Value("${email.host}")
    String emailHost;

    @Value("${email.user}")
    String emailUser;

    @Value("${email.port}")
    int emailPort;

    @Value("${email.password}")
    String emailPassword;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);

        mailSender.setUsername(emailUser);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public static void main(String[] args) {

        SpringApplication.run(DemotestApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    // On met la dépendance ici plutot que dans l'application sinon ça fait une dépendance récursive
    @Bean // @Bean facilement remplaçables quand on fait des tests unitaires
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // a chaque fois que j'aurais besoin d'un objet password encoder -> je recupère un BCryptPasswordEncoder
    }
}

