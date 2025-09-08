// * `userDetailsService()`: Personnalise la façon dont les informations de l'utilisateur sont récupérées.

package com.hrizzon2.demotest.security;

import com.hrizzon2.demotest.user.dao.UserDao;
import com.hrizzon2.demotest.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service chargé de charger les détails de l'utilisateur
 * pour l'authentification Spring Security, en fonction de son email.
 * Il distingue les Admins et les Stagiaires.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserDao userDao;


    @Autowired
    public AppUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Recherche un utilisateur (Admin ou Stagiaire) par son email.
     * Retourne un objet UserDetails utilisé par Spring Security.
     *
     * @param email Email de l'utilisateur à authentifier.
     * @return UserDetails avec les informations de sécurité.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("Recherche utilisateur par email : " + email);

        try {
            Optional<User> userOpt = userDao.findByEmail(email);
            System.out.println("Résultat findByEmail : " + userOpt.isPresent());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Utilisateur trouvé : " + user.getEmail());
                System.out.println("Password hash : " + user.getPassword());
                System.out.println("Enabled : " + user.isEnabled());

                AppUserDetails details = new AppUserDetails(user);
                System.out.println("AppUserDetails créé");
                return details;
            } else {
                System.out.println("Aucun utilisateur trouvé");
                throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
            }
        } catch (Exception e) {
            System.out.println("Exception dans loadUserByUsername : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

//        if (optionalStagiaire.isPresent()) {
//            return new AppUserDetails(optionalStagiaire.get());
//        }
//
//        Optional<Admin> optionalAdmin = adminDao.findByEmail(email);
//
//        if (optionalAdmin.isPresent()) {
//            return new AppUserDetails(optionalAdmin.get());
//        }
//
//        throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
//    }
    }
}
