package com.hrizzon2.demotest.controller;


import com.hrizzon2.demotest.dao.UserDao;
import com.hrizzon2.demotest.model.User;
import com.hrizzon2.demotest.security.AppUserDetails;
import com.hrizzon2.demotest.security.ISecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class AuthController {

    protected UserDao userDao;
    protected PasswordEncoder passwordEncoder;
    protected AuthenticationProvider authenticationProvider;
    protected ISecurityUtils securityUtils;

    @Autowired
    public AuthController(UserDao userDao, PasswordEncoder passwordEncoder, ISecurityUtils securityUtils) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.securityUtils = securityUtils;
    }

//    // Méthode d'inscription d'un user
//    @PostMapping("/inscription")
//    public ResponseEntity<User> inscription(@RequestBody @Valid User user) {
//

    /// /        user.setRole(Role.USER);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userDao.save(user);
//
//        // On masque le mdp
//        user.setPassword(null);
//        return new ResponseEntity<>(user, HttpStatus.CREATED);
//    }

    // TODO trouver comment gérer l'authentification suite à 1ère connexion (!= d'inscription) avec changement du mdp


    // Méthode de connexion de l'utilisateur
    @PostMapping("/connexion")
    public ResponseEntity<String> connexion(@RequestBody @Valid User user) {

        try {
            AppUserDetails userDetails = (AppUserDetails) authenticationProvider.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    user.getPassword()))
                    .getPrincipal();

            return new ResponseEntity<>(securityUtils.generateToken(userDetails), HttpStatus.OK);

        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
