package com.hrizzon2.demotest.controller;


import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.dao.UserDao;
import com.hrizzon2.demotest.dto.ChangePasswordDto;
import com.hrizzon2.demotest.dto.ValidationEmailDto;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.User;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import com.hrizzon2.demotest.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Endpoints recommandés pour un Auth complet
 * POST /inscription : inscription
 * <p>
 * POST /connexion : connexion
 * <p>
 * POST /validate-email : validation email
 * <p>
 * POST /change-password : changement mdp (première connexion/oubli)
 * <p>
 * (optionnel) POST /forgot-password + POST /reset-password : récupération mdp
 */

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    protected UserDao userDao;
    protected StagiaireDao stagiaireDao;
    protected PasswordEncoder passwordEncoder;
    protected AuthenticationProvider authenticationProvider;
    protected ISecurityUtils securityUtils;
    protected EmailService emailService;

    @Autowired
    public AuthController(UserDao userDao, StagiaireDao stagiaireDao, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, ISecurityUtils securityUtils, EmailService emailService) {
        this.userDao = userDao;
        this.stagiaireDao = stagiaireDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.securityUtils = securityUtils;
        this.emailService = emailService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody @Valid User user) throws IOException {

        // Vérifier si l'email existe déjà
        if (stagiaireDao.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un utilisateur avec cet email existe déjà.");
        }

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(Integer.valueOf(UUID.randomUUID().toString()));
        stagiaire.setEmail(user.getEmail());
        stagiaire.setPassword(passwordEncoder.encode(user.getPassword()));
        stagiaire.setActive(false); // NE PAS ACTIVER AVANT VALIDATION

        String tokenValidationEmail = UUID.randomUUID().toString();
        stagiaire.setJetonVerificationEmail(tokenValidationEmail);
        stagiaire.setPremiereConnexion(true); // Ajoute ce flag


        stagiaireDao.save(stagiaire);
        emailService.sendEmailValidationToken(stagiaire.getEmail(), tokenValidationEmail);

        //on masque le mot de passe et le jeton de vérification
        stagiaire.setPassword(null);
        stagiaire.setJetonVerificationEmail(null);
        return new ResponseEntity<>(stagiaire, HttpStatus.CREATED);
    }
//Remarques :
//
//active (ou enabled) doit être false tant que le mail n’a pas été validé.
//
//premiereConnexion doit être true: cela permettra de forcer la modification du mot de passe.

    //Validation des emails déjà utilisés
//
//Avant inscription, vérifie si un utilisateur existe déjà avec cet email (sinon, duplication possible).
    @PostMapping("/validate-email")
    public ResponseEntity<?> validateEmail(@RequestBody ValidationEmailDto validationEmailDto) {

        Optional<Stagiaire> stagiaireOpt = stagiaireDao.findByJetonVerificationEmail(validationEmailDto.getToken());

        if (stagiaireOpt.isPresent()) {
            Stagiaire stagiaire = stagiaireOpt.get();
            stagiaire.setJetonVerificationEmail(null);
            stagiaire.setActive(true); // Active le compte
            stagiaireDao.save(stagiaire);
            // On peut retourner le flag premiereConnexion pour que le front affiche la page de changement de mdp
            return ResponseEntity.ok(Map.of(
                    "email", stagiaire.getEmail(),
                    "premiereConnexion", stagiaire.isPremiereConnexion()
            ));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lien de validation invalide.");
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Optional<Stagiaire> stagiaireOpt = stagiaireDao.findByEmail(changePasswordDto.getEmail());

        if (stagiaireOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        Stagiaire stagiaire = stagiaireOpt.get();

        if (!stagiaire.isPremiereConnexion()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Changement de mot de passe non autorisé.");
        }

        stagiaire.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        stagiaire.setPremiereConnexion(false); // Il a changé son mdp: plus besoin de forcer le changement
        stagiaireDao.save(stagiaire);
        return ResponseEntity.ok("Mot de passe modifié avec succès.");
    }


    // Méthode de connexion de l'user
    @PostMapping("/connexion")
    public ResponseEntity<String> connexion(@RequestBody @Valid User user) {

        Optional<Stagiaire> stagiaireOpt = stagiaireDao.findByEmail(user.getEmail());
        if (stagiaireOpt.isPresent() && !stagiaireOpt.get().isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Compte non activé. Merci de valider votre email.");
        }

        try {
            AppUserDetails userDetails = (AppUserDetails) authenticationProvider
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    user.getPassword()))
                    .getPrincipal();

            return new ResponseEntity<>(securityUtils.generateToken(userDetails), HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


//     ENDPOINTS a implémenter :
//    - POST /change-password : changement mdp (première connexion/oubli)
//    - (optionnel) POST /forgot-password + POST /reset-password : récupération mdp


//    Gestion du rôle/activation de compte
//
//Ajoute un flag “enabled” ou “isActive” sur l’entité User/Stagiaire, à mettre à “false” par défaut, à “true” lors de la validation email.
//
//Empêche la connexion tant que le compte n’est pas validé (sinon faille de sécurité).
}
