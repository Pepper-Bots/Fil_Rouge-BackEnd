// * `userDetailsService()`: Personnalise la façon dont les informations de l'utilisateur sont récupérées.

package com.hrizzon2.demotest.security;

import com.hrizzon2.demotest.dao.AdminDao;
import com.hrizzon2.demotest.dao.StagiaireDao;
import com.hrizzon2.demotest.model.Admin;
import com.hrizzon2.demotest.model.Stagiaire;
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

    protected StagiaireDao stagiaireDao;
    protected AdminDao adminDao;

    @Autowired
    public AppUserDetailsService(StagiaireDao stagiaireDao, AdminDao adminDao) {
        this.stagiaireDao = stagiaireDao;
        this.adminDao = adminDao;
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

        Optional<Stagiaire> optionalStagiaire = stagiaireDao.findByEmail(email);

        if (optionalStagiaire.isPresent()) {
            return new AppUserDetails(optionalStagiaire.get());
        }

        Optional<Admin> optionalAdmin = adminDao.findByEmail(email);

        if (optionalAdmin.isPresent()) {
            return new AppUserDetails(optionalAdmin.get());
        }

        throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);
    }
}
