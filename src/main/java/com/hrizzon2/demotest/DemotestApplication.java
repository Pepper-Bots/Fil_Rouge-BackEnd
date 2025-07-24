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

/**
 * Classe principale de l'application Spring Boot pour la gestion des dossiers de candidature.
 * <p>
 * Cette classe configure les beans essentiels de l'application, notamment :
 * <ul>
 *   <li>Le service d'envoi d'emails (JavaMailSender)</li>
 *   <li>L'encodeur de mots de passe (PasswordEncoder)</li>
 *   <li>La configuration du fuseau horaire par défaut</li>
 * </ul>
 * </p>
 *
 * @author Votre nom
 * @version 1.0
 * @since 1.0
 */
// Annotation : pour signifier que le programme se base sur ce qui est écrit ici, pas ailleurs
@SpringBootApplication
public class DemotestApplication {

    /**
     * Hôte du serveur SMTP pour l'envoi d'emails.
     * Configuré via la propriété {@code email.host} dans application.properties.
     */
    @Value("${email.host}")
    String emailHost;

    /**
     * Nom d'utilisateur pour l'authentification SMTP.
     * Configuré via la propriété {@code email.user} dans application.properties.
     */
    @Value("${email.user}")
    String emailUser;

    /**
     * Port du serveur SMTP.
     * Configuré via la propriété {@code email.port} dans application.properties.
     */
    @Value("${email.port}")
    int emailPort;

    /**
     * Mot de passe pour l'authentification SMTP.
     * Configuré via la propriété {@code email.password} dans application.properties.
     */
    @Value("${email.password}")
    String emailPassword;

    /**
     * Point d'entrée principal de l'application Spring Boot.
     *
     * @param args arguments de ligne de commande passés à l'application
     */
    public static void main(String[] args) {
        SpringApplication.run(DemotestApplication.class, args);
    }

    /**
     * Méthode d'initialisation appelée après la construction du bean.
     * <p>
     * Configure le fuseau horaire par défaut de l'application sur UTC
     * pour assurer une cohérence dans la gestion des dates/heures,
     * particulièrement important dans un contexte multi-utilisateurs
     * ou de déploiement distribué.
     * </p>
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Crée et configure le bean JavaMailSender pour l'envoi d'emails.
     * <p>
     * Configure un client SMTP avec les paramètres suivants :
     * <ul>
     *   <li>Support du protocole SMTP avec authentification</li>
     *   <li>Activation de STARTTLS pour la sécurité</li>
     *   <li>Mode debug activé pour le développement</li>
     * </ul>
     * </p>
     *
     * @return une instance configurée de JavaMailSender
     * @see JavaMailSender
     * @see JavaMailSenderImpl
     */
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

    /**
     * Crée le bean PasswordEncoder pour l'encodage sécurisé des mots de passe.
     * <p>
     * Utilise BCryptPasswordEncoder qui implémente l'algorithme de hachage BCrypt,
     * reconnu pour sa sécurité et sa résistance aux attaques par force brute.
     * Ce bean est défini ici plutôt que dans une classe de service pour éviter
     * les dépendances circulaires.
     * </p>
     *
     * @return une instance de BCryptPasswordEncoder
     * @see PasswordEncoder
     * @see BCryptPasswordEncoder
     */
    // On met la dépendance ici plutot que dans l'application sinon ça fait une dépendance récursive
    @Bean // @Bean facilement remplaçables quand on fait des tests unitaires
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // a chaque fois que j'aurais besoin d'un objet password encoder -> je recupère un BCryptPasswordEncoder
    }
}

