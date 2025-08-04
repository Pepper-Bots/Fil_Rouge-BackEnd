package com.hrizzon2.demotest.security;
// Sécurité Spring Security

import com.hrizzon2.demotest.user.model.Admin;
import com.hrizzon2.demotest.user.model.Stagiaire;
import com.hrizzon2.demotest.user.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implémentation personnalisée de UserDetails pour Spring Security,
 * qui encapsule un utilisateur de type Stagiaire ou Admin.
 */
@Getter
public class AppUserDetails implements UserDetails {

    protected User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Attribution des rôles selon le type d'utilisateur
        if (user instanceof Stagiaire) {
            return List.of(new SimpleGrantedAuthority("ROLE_STAGIAIRE"));
        } else if (user instanceof Admin) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(); // Aucun rôle si le type est inconnu.
        }
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return true; // à adapter si besoin
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true; // idem
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true; // idem
//    }

    // Gestion spécifique pour forcer le changement de mot de passe
    public boolean isPremiereConnexion() {
        if (this.user instanceof Stagiaire stagiaire) {
            return stagiaire.isPremiereConnexion();
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled(); // tu peux ajouter un champ dans User si besoin
    }


    public String getRole() {
        return ((User) this.getUser()).getNomRole(); // ou .getNomRole().toString()
    }

}