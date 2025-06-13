package com.hrizzon2.demotest.controller;


import com.hrizzon2.demotest.dto.ChangePasswordDto;
import com.hrizzon2.demotest.dto.ValidationEmailDto;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.User;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import com.hrizzon2.demotest.security.IsAdmin;
import com.hrizzon2.demotest.service.EmailService;
import com.hrizzon2.demotest.service.User.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/auth")
public class AuthController {

    protected UserService userService;
    protected PasswordEncoder passwordEncoder;
    protected AuthenticationProvider authenticationProvider;
    protected ISecurityUtils securityUtils;
    protected EmailService emailService;

    @Autowired
    public AuthController(EmailService emailService, UserService userService, PasswordEncoder passwordEncoder, AuthenticationProvider authenticationProvider, ISecurityUtils securityUtils) {
        this.emailService = emailService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.securityUtils = securityUtils;

    }

    /**
     * Inscription d'un utilisateur (par défaut, utilisateur non activé)
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody @Validated User user) throws IOException {

        // Vérifier si l'email existe déjà
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Un utilisateur avec cet email existe déjà.");
        }

        // Initialisation
        user.setEnabled(false); // A activer à la validation email
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Génération du token de validation d'email
        String tokenValidationEmail = UUID.randomUUID().toString();
        user.setJetonVerificationEmail(tokenValidationEmail);


        userService.save(user);
        emailService.sendActivationEmail(user.getEmail(), tokenValidationEmail);

        // Masquer le password et le token dans la réponse
        user.setPassword(null);
        user.setJetonVerificationEmail(null);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Création d'un Stagiaire (admin only)
     */
    @PostMapping("/stagiaire")
    @IsAdmin
    public ResponseEntity<?> createStagiaire(@RequestBody @Valid Stagiaire stagiaire) {

        // Initialisation des champs
        stagiaire.setEnabled(false); // Compte à activer par email
        stagiaire.setPremiereConnexion(true);
        stagiaire.setPassword(passwordEncoder.encode(stagiaire.getPassword()));

        // Génération du token de validation d'email
        String tokenValidationEmail = UUID.randomUUID().toString();
        stagiaire.setJetonVerificationEmail(tokenValidationEmail);

        userService.save(stagiaire);
        emailService.sendActivationEmail(stagiaire.getEmail(), tokenValidationEmail);

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

    /**
     * Validation du compte par email (pour tous les users)
     */
    @PostMapping("/validate-email")
    public ResponseEntity<?> validateEmail(@RequestBody ValidationEmailDto validationEmailDto) {

        boolean success = userService.validateEmail(validationEmailDto.getToken());
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "message", "Compte activé avec succès !"));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lien de validation invalide.");
    }

    /**
     * Changement de mot de passe lors de la première connexion ou oubli
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {

        try {
            userService.changePasswordFirstLogin(changePasswordDto.getEmail(), changePasswordDto.getNewPassword());
            return ResponseEntity.ok("Mot de passe modifié avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Connexion (login)
     */
    @PostMapping("/connexion")
    public ResponseEntity<String> connexion(@RequestBody @Valid User user) {

        // Optionnel : Vérifier si le compte est activé (enabled)
        Optional<User> userOpt = userService.findByEmail(user.getEmail());
        if (userOpt.isEmpty() || !userOpt.get().isEnabled()) {
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

    /**
     * Demande de reset password (forgot password)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            userService.requestPasswordReset(body.get("email"));
            return ResponseEntity.ok().build(); // Compliant: Setting 200 for a successful operation
        } catch (Exception e) {
            // On ne précise pas si l'email existe ou pas (par sécurité)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Compliant: Setting 500 for exception
        }
    }

    /**
     * Reset du mot de passe via token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            userService.resetPassword(body.get("token"), body.get("newPassword"));
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


}
