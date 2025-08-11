package com.hrizzon2.demotest.inscription.dao;

import com.hrizzon2.demotest.formation.model.Formation;
import com.hrizzon2.demotest.inscription.model.Inscription;
import com.hrizzon2.demotest.inscription.model.enums.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscriptionDao extends JpaRepository<Inscription, Integer> {

    Optional<Inscription> findByStagiaireIdAndStatut(Integer stagiaireId, StatutInscription statut);

    List<Inscription> findByStatut(StatutInscription statut);

    // ✅ Ajoutez cette méthode pour récupérer les inscriptions d'un stagiaire
    List<Inscription> findByStagiaireId(Integer stagiaireId);


    // ✅ Modifiez cette méthode pour retourner directement les formations
    @Query("SELECT i.formation FROM Inscription i WHERE i.stagiaire.id = :stagiaireId")
    List<Formation> findFormationsByStagiaire(@Param("stagiaireId") Integer stagiaireId);
}
