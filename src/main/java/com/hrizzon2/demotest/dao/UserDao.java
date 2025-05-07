package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role); // Exemple simple

    // TODO créer une méthode findByAdmin pour les notifs

}
