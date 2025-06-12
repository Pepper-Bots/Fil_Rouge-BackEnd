package com.hrizzon2.demotest.dao;
// accès BDD (repository)

import com.hrizzon2.demotest.model.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DossierDao extends JpaRepository<Dossier, Integer> {

    Optional<Dossier> findById(int id);

    Optional<Dossier> findByCodeDossier(String codeDossier);

    /**
     * Pour charger le dossier à partir de l’ID du stagiaire.
     * On suppose qu’il n’y a qu’un seul Dossier actif par stagiaire à un instant t.
     */
    Optional<Dossier> findByStagiaireId(Integer stagiaireId);

}

// TODO **Conserver** :
//  dossiers d’inscription.