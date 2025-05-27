package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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
//    @Query("SELECT s FROM Stagiaire s WHERE s.email = :email")
    Optional<Stagiaire> findByEmail(String email);

    @Query("SELECT s FROM Stagiaire s JOIN s.ville v WHERE v.idVille = :idVille")
    List<Stagiaire> findByidVille(@Param("idVille") Integer idVille);

    @Query("SELECT s FROM Stagiaire s JOIN s.inscriptions i WHERE i.dateInscription BETWEEN :dateDebut AND :dateFin")
    List<Stagiaire> findBetweenDates(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);

    @Query("SELECT s FROM Stagiaire s JOIN s.dossiers d JOIN d.statutDossier sd WHERE sd.nomStatut = :statut")
    List<Stagiaire> findByDossierStatut(@Param("statut") String statut);


}
