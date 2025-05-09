package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository qui parle à la base de données

/**
 * DAO pour la gestion des entités Stagiaire.
 * Fournit des méthodes CRUD et une méthode de recherche par email.
 */
@Repository
public interface StagiaireDao extends JpaRepository<Stagiaire, Integer> {

    /**
     * Recherche un stagiaire par son email.
     *
     * @param email Email du stagiaire
     * @return Optional contenant le stagiaire s’il existe
     */
    Optional<Stagiaire> findByEmail(String email);
}
