package com.hrizzon2.demotest.user.service.User;

import com.hrizzon2.demotest.service.EmailService;
import com.hrizzon2.demotest.user.dao.UserDao;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService = new EmailService();

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    // Validation du compte via email
    @Override
    public boolean validateEmail(String token) {
        Optional<User> userOpt = userDao.findByJetonVerificationEmail(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setJetonVerificationEmail(null);  // Invalide le token
            user.setEnabled(true); // Active le compte (assure-toi d’avoir un champ "active" ou "enabled")
            userDao.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void changePasswordFirstLogin(String email, CharSequence newPassword) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur avec cet email"));

        // Si user est Stagiaire, check premiereConnexion
        if (user instanceof Stagiaire stagiaire) {
            if (!stagiaire.isPremiereConnexion()) {
                throw new RuntimeException("Vous devez vous connecter pour changer votre mot de passe");
            }
            stagiaire.setPremiereConnexion(false);

        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void requestPasswordReset(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur avec cet email"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        userDao.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userDao.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token de réinitialisation invalide"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Invalider le token après usage
        userDao.save(user);
    }


    @Override
    public boolean existsByEmail(String email) {
        return userDao.findByEmail(email).isPresent();
    }

    @Override
    public User save(User user) {
        return userDao.save(user);
    }
}
