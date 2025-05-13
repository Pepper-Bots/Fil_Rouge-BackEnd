package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    // Tu peux définir des méthodes génériques pour les utilisateurs ici si nécessaire
    // Par exemple, trouver un utilisateur par son nom d'utilisateur (si c'est une propriété de User)
    // List<User> findByUsername(String username);
    // TODO créer une méthode findByAdmin pour les notifs

}
