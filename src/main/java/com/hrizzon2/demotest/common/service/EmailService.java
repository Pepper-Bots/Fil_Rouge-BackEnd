package com.hrizzon2.demotest.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailValidationToken(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Validation de votre email");
        message.setText("Pour valider votre email, veuillez cliquer sur le lien suivant : "
                + "http://localhost:4200/validate-email/" + token);
        mailSender.send(message);
    }

//    // Alias pour cohérence avec StagiaireServiceImpl
//    public void sendActivationEmail(String to, String activationToken) {
//        // Tu réutilises la méthode existante, comme ça pas de duplication !
//        sendEmailValidationToken(to, activationToken);
//    }

    public void sendResetPasswordEmail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Pour réinitialiser votre mot de passe, cliquez sur le lien suivant : "
                + "http://localhost:4200/reset-password/" + resetToken);
        mailSender.send(message);
    }

    public void sendActivationEmail(String email, String tokenValidationEmail) {
        sendEmailValidationToken(email, tokenValidationEmail);
    }
}
