package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.user.model.Ville;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Interface DAO (Repository) pour l'entité {@link Ville}.
 *
 * <p>Permet l'accès aux opérations CRUD et des requêtes personnalisées sur la table "ville".</p>
 */
public interface VilleDao extends JpaRepository<Ville, Integer> {

    /**
     * Recherche des villes dont le nom contient une chaîne donnée, sans tenir compte de la casse.
     *
     * @param nom chaîne partielle ou totale à rechercher dans le nom de la ville
     * @return liste des villes correspondantes
     */
    List<Ville> findByNomVilleContainingIgnoreCase(String nom);
}