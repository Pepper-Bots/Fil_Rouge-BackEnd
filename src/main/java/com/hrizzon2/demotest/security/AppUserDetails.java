package com.hrizzon2.demotest.security;
// Sécurité Spring Security

import com.hrizzon2.demotest.model.Admin;
import com.hrizzon2.demotest.model.Stagiaire;
import com.hrizzon2.demotest.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AppUserDetails implements UserDetails {

    protected User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        boolean IsStagiaire = user instanceof Stagiaire;

        if (IsStagiaire) {
            return List.of(new SimpleGrantedAuthority("ROLE_STAGIAIRE"));
        } else {
            Admin admin = (Admin) user;
        }
        ;
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
