package com.hrizzon2.demotest.dao;

import com.hrizzon2.demotest.model.StatutDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatutDossierDao extends JpaRepository<StatutDossier, Integer> {

    /**
     * Exemple : findByNom("INCOMPLET") ou findByNom("EN_COURS") ou findByNom("COMPLET").
     */

    Optional<StatutDossier> findByNomStatut(String nomStatut);

}
